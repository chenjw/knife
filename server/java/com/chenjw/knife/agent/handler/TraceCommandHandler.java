package com.chenjw.knife.agent.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.filter.Depth0Filter;
import com.chenjw.knife.agent.filter.DepthFilter;
import com.chenjw.knife.agent.filter.EnterLeavePrintFilter;
import com.chenjw.knife.agent.filter.ExceptionFilter;
import com.chenjw.knife.agent.filter.Filter;
import com.chenjw.knife.agent.filter.FilterInvocationListener;
import com.chenjw.knife.agent.filter.InstrumentClassLoaderFilter;
import com.chenjw.knife.agent.filter.InstrumentEnterLeaveFilter;
import com.chenjw.knife.agent.filter.InstrumentFilter;
import com.chenjw.knife.agent.filter.InvokePrintFilter;
import com.chenjw.knife.agent.filter.PatternMethodFilter;
import com.chenjw.knife.agent.filter.ProxyMethodFilter;
import com.chenjw.knife.agent.filter.SystemOperationFilter;
import com.chenjw.knife.agent.filter.TimesCountFilter;
import com.chenjw.knife.agent.filter.TimingFilter;
import com.chenjw.knife.agent.filter.TimingStopFilter;
import com.chenjw.knife.agent.filter.TraceMethodFilter;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.StringHelper;

public class TraceCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {

		MethodInfo methodInfo = findMethod(args);
		if (methodInfo != null) {
			configStrategy(args, methodInfo);
			trace(methodInfo);
		}

	}

	private void trace(MethodInfo methodInfo) {
		ClassLoaderHelper.resetClassLoader(methodInfo.getMethod()
				.getDeclaringClass());
		Profiler.profileEnterLeave(methodInfo.getMethod());
	}

	private void configStrategy(Args args, MethodInfo methodInfo)
			throws Exception {
		int traceNum = 1;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			traceNum = Integer.parseInt(nOptions.get("trace-num"));
		}
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new ExceptionFilter());

		filters.add(new TimingStopFilter());
		Map<String, String> tOptions = args.option("-t");
		filters.add(new InstrumentClassLoaderFilter());
		if (tOptions != null) {
			filters.add(new InstrumentFilter());
		}
		filters.add(new InstrumentEnterLeaveFilter());
		Map<String, String> fOptions = args.option("-f");
		if (fOptions != null) {
			filters.add(new PatternMethodFilter(fOptions
					.get("filter-expression")));
		}
		filters.add(new ProxyMethodFilter());
		filters.add(new TraceMethodFilter(methodInfo.getThisObject(),
				methodInfo.getClazz(), methodInfo.getMethod()));
		filters.add(new TimesCountFilter(traceNum));
		filters.add(new EnterLeavePrintFilter());
		filters.add(new DepthFilter());
		if (tOptions == null) {
			filters.add(new Depth0Filter());
		}
		filters.add(new TimingFilter());
		filters.add(new InvokePrintFilter());

		Profiler.listener = new FilterInvocationListener(filters);
	}

	private class MethodInfo {
		private Object thisObject;
		private Class<?> clazz;
		private Method method;

		public Object getThisObject() {
			return thisObject;
		}

		public void setThisObject(Object thisObject) {
			this.thisObject = thisObject;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

	}

	private MethodInfo findMethod(Args args) throws Exception {
		MethodInfo methodInfo = new MethodInfo();
		String m = args.arg("trace-expression");
		m = m.trim();
		Method method = null;
		if (StringHelper.isNumeric(m)) {
			method = ((Method[]) ServiceRegistry.getService(
					ContextService.class).get(Constants.METHOD_LIST))[Integer
					.parseInt(m)];

		} else {
			if (m.indexOf(".") != -1) {
				String className = StringHelper.substringBeforeLast(m, ".");
				m = StringHelper.substringAfterLast(m, ".");
				Class<?> clazz = NativeHelper.findLoadedClass(className);
				if (clazz == null) {
					clazz = Helper.findClass(className);
				}
				if (clazz == null) {

					Agent.sendResult(ResultHelper.newErrorResult("class "
							+ className + " not found!"));
					return null;
				}
				Method[] methods = ReflectHelper.getMethods(clazz);
				for (Method tm : methods) {
					if (StringHelper.equals(tm.getName(), m)) {
						if (Modifier.isStatic(tm.getModifiers())) {
							method = tm;
							break;
						}
					}
				}
			} else {
				Object obj = ServiceRegistry.getService(ContextService.class)
						.get(Constants.THIS);
				if (obj == null) {
					Agent.sendResult(ResultHelper.newErrorResult("not found!"));
					return null;
				}
				Method[] methods = obj.getClass().getMethods();
				for (Method tm : methods) {
					if (StringHelper.equals(tm.getName(), m)) {
						method = tm;
						break;
					}
				}
			}
		}
		if (method == null) {
			Agent.sendResult(ResultHelper.newErrorResult("cant find method!"));
			return null;
		}
		methodInfo.setMethod(method);
		if (Modifier.isStatic(method.getModifiers())) {
			methodInfo.setClazz(method.getDeclaringClass());
			methodInfo.setThisObject(null);
		} else {
			Object thisObject = ServiceRegistry
					.getService(ContextService.class).get(Constants.THIS);
			methodInfo.setThisObject(thisObject);
			methodInfo.setClazz(thisObject.getClass());
		}
		return methodInfo;
	}

	public static String getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = Helper.makeClassName(classes[i]);
		}
		return StringHelper.join(classNames, ",");
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("trace [-f <filter-expression>] [-n <trace-num>] [-t] <trace-expression>");

	}
}
