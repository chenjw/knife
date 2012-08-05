package com.chenjw.knife.agent.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.filter.DepthFilter;
import com.chenjw.knife.agent.handler.log.filter.EnterLeavePrintFilter;
import com.chenjw.knife.agent.handler.log.filter.ExceptionFilter;
import com.chenjw.knife.agent.handler.log.filter.Filter;
import com.chenjw.knife.agent.handler.log.filter.InstrumentFilter;
import com.chenjw.knife.agent.handler.log.filter.InvokePrintFilter;
import com.chenjw.knife.agent.handler.log.filter.PatternMethodFilter;
import com.chenjw.knife.agent.handler.log.filter.SystemOperationFilter;
import com.chenjw.knife.agent.handler.log.filter.TimesCountFilter;
import com.chenjw.knife.agent.handler.log.filter.TimingFilter;
import com.chenjw.knife.agent.handler.log.filter.TimingStopFilter;
import com.chenjw.knife.agent.handler.log.filter.TraceMethodFilter;
import com.chenjw.knife.agent.handler.log.listener.FilterInvocationListener;
import com.chenjw.knife.agent.service.ContextManager;
import com.chenjw.knife.agent.util.StringHelper;

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
		if (Modifier.isStatic(methodInfo.getMethod().getModifiers())) {
			Profiler.traceClass(methodInfo.getClazz(), methodInfo.getMethod()
					.getName());
		} else {
			Profiler.traceObject(methodInfo.getThisObject(), methodInfo
					.getMethod().getName());
		}

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

		filters.add(new InstrumentFilter());
		Map<String, String> options = args.option("-f");
		if (options != null) {
			filters.add(new PatternMethodFilter(options.get("filter-expretion")));
		}
		filters.add(new TraceMethodFilter(methodInfo.getThisObject(),
				methodInfo.getClazz(), methodInfo.getMethod()));
		filters.add(new TimesCountFilter(traceNum));
		filters.add(new EnterLeavePrintFilter());
		filters.add(new DepthFilter());
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
				Class<?> clazz = Helper.findClass(className);
				if (clazz == null) {
					Agent.println("class " + className + " not found!");
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
					Agent.println("not found!");
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
			Agent.println("cant find method!");
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
		argDef.setDef("[-f <filter-expretion>] [-n <trace-num>] <trace-expretion>");
		argDef.setDesc("trace an invocation on the target object.");

		argDef.addOptionDesc(
				"trace-expretion",
				"set <classname> to find static fields or methods , if <classname> not set , will apply to target object.");
		argDef.addOptionDesc("-f",
				"set <filter-expretion> to filter the invocation you dont care.");
		argDef.addOptionDesc(
				"-n",
				"set <classname> to find static fields or methods , if <classname> not set , will apply to target object.");

	}
}
