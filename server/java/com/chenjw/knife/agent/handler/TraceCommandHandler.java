package com.chenjw.knife.agent.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.constants.Constants;
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
import com.chenjw.knife.agent.filter.SystemOperationFilter;
import com.chenjw.knife.agent.filter.TimesCountFilter;
import com.chenjw.knife.agent.filter.TimingFilter;
import com.chenjw.knife.agent.filter.TimingStopFilter;
import com.chenjw.knife.agent.filter.TraceMethodFilter;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;
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
					.get("filter-expretion")));
		}
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
		String m = args.arg("trace-expretion");
		m = m.trim();
		Method method = null;
		if (StringHelper.isNumeric(m)) {
			method = ((Method[]) ContextManager.getInstance().get(
					Constants.METHOD_LIST))[Integer.parseInt(m)];

		} else {
			if (m.indexOf(".") != -1) {
				String className = StringHelper.substringBeforeLast(m, ".");
				m = StringHelper.substringAfterLast(m, ".");
				Class<?> clazz = NativeHelper.findLoadedClass(className);
				if (clazz == null) {
					clazz = Helper.findClass(className);
				}
				if (clazz == null) {
					Agent.info("class " + className + " not found!");
					return null;
				}
				Method[] methods = clazz.getMethods();
				for (Method tm : methods) {
					if (StringHelper.equals(tm.getName(), m)) {
						if (Modifier.isStatic(tm.getModifiers())) {
							method = tm;
							break;
						}
					}
				}
			} else {
				Object obj = ContextManager.getInstance().get(Constants.THIS);
				if (obj == null) {
					Agent.info("not found!");
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
			Agent.info("cant find method!");
			return null;
		}
		methodInfo.setMethod(method);
		if (Modifier.isStatic(method.getModifiers())) {
			methodInfo.setClazz(method.getDeclaringClass());
			methodInfo.setThisObject(null);
		} else {
			Object thisObject = ContextManager.getInstance()
					.get(Constants.THIS);
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
		argDef.setCommandName("trace");
		argDef.setDef("[-f <filter-expretion>] [-n <trace-num>] [-t] <trace-expretion>");
		argDef.setDesc("trace an invocation on the target object.");

		argDef.addOptionDesc(
				"trace-expretion",
				"Input 'package.ClassName.method' to trace static method, or 'method' to trace the method of target object.");
		argDef.addOptionDesc("-f",
				"set <filter-expretion> to filter the invocation you dont care.");
		argDef.addOptionDesc("-t",
				"is need output the method trace of the invocation.");
		argDef.addOptionDesc("-n <trace-num>", "trace times");

	}
}
