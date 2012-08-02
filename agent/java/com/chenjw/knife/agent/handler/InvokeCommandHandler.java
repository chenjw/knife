package com.chenjw.knife.agent.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javassist.Modifier;

import org.apache.commons.lang.StringUtils;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.ParseHelper;
import com.chenjw.knife.agent.handler.log.Profiler;
import com.chenjw.knife.agent.handler.log.filter.Depth0Filter;
import com.chenjw.knife.agent.handler.log.filter.DepthFilter;
import com.chenjw.knife.agent.handler.log.filter.ExceptionFilter;
import com.chenjw.knife.agent.handler.log.filter.FixThreadFilter;
import com.chenjw.knife.agent.handler.log.filter.InstrumentFilter;
import com.chenjw.knife.agent.handler.log.filter.InvokeFinishFilter;
import com.chenjw.knife.agent.handler.log.filter.InvokePrintFilter;
import com.chenjw.knife.agent.handler.log.filter.PatternMethodFilter;
import com.chenjw.knife.agent.handler.log.filter.SystemOperationFilter;
import com.chenjw.knife.agent.handler.log.filter.TimingFilter;
import com.chenjw.knife.agent.handler.log.listener.FilterInvocationListener;

public class InvokeCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		boolean isTrace = args.option("-t") != null;
		if (isTrace) {
			configTraceStrategy(args);
		} else {
			configNoTraceStrategy(args);
		}

		invokeMethod(isTrace, args.arg("invoke-expretion"));
	}

	private void configTraceStrategy(Args args) throws Exception {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new FixThreadFilter(Thread.currentThread()));
		filters.add(new ExceptionFilter());
		filters.add(new TimingFilter());
		filters.add(new InstrumentFilter());

		Map<String, String> options = args.option("-f");
		if (options != null) {
			filters.add(new PatternMethodFilter(options.get("filter-expretion")));
		}
		filters.add(new DepthFilter());
		filters.add(new InvokeFinishFilter());
		filters.add(new InvokePrintFilter());
		Profiler.listener = new FilterInvocationListener(filters);
	}

	private void configNoTraceStrategy(Args args) throws Exception {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new FixThreadFilter(Thread.currentThread()));
		filters.add(new ExceptionFilter());
		filters.add(new TimingFilter());
		Map<String, String> options = args.option("-f");
		if (options != null) {
			filters.add(new PatternMethodFilter(options.get("filter-expretion")));
		}
		filters.add(new DepthFilter());
		filters.add(new Depth0Filter());
		filters.add(new InvokeFinishFilter());
		filters.add(new InvokePrintFilter());

		Profiler.listener = new FilterInvocationListener(filters);
	}

	private void invokeMethod(boolean isTrace, String methodSig)
			throws Exception {

		String argStr = methodSig;
		String m = StringUtils.substringBefore(argStr, "(");
		m = m.trim();
		Method method = null;
		if (StringUtils.isNumeric(m)) {
			method = ((Method[]) Context.get(Constants.METHOD_LIST))[Integer
					.parseInt(m)];
		} else {
			if (m.indexOf(".") != -1) {
				String className = StringUtils.substringBeforeLast(m, ".");
				m = StringUtils.substringAfterLast(m, ".");
				Class<?> clazz = Helper.findClass(className);
				if (clazz == null) {
					Agent.println("class " + className + " not found!");
					return;
				}
				Method[] methods = clazz.getMethods();
				for (Method tm : methods) {
					if (StringUtils.equals(tm.getName(), m)) {
						if (Modifier.isStatic(tm.getModifiers())) {
							method = tm;
							break;
						}
					}
				}

			} else {
				Object obj = Context.get(Constants.THIS);
				if (obj == null) {
					Agent.println("not found!");
					return;
				}
				Method[] methods = obj.getClass().getMethods();
				for (Method tm : methods) {
					if (StringUtils.equals(tm.getName(), m)) {
						method = tm;
						break;
					}
				}
			}
		}
		if (method == null) {
			Agent.println("cant find method!");
			return;
		}
		Object[] mArgs = ParseHelper.parseMethodArgs(
				StringUtils.substringBeforeLast(
						StringUtils.substringAfter(argStr, "("), ")"),
				method.getParameterTypes());
		if (Modifier.isStatic(method.getModifiers())) {
			invoke(isTrace, method, null, mArgs);
		} else {
			invoke(isTrace, method, Context.get(Constants.THIS), mArgs);
		}
	}

	private void invoke(boolean isTrace, Method method, Object thisObject,
			Object[] args) throws Exception {
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		Class<?> clazz = null;
		if (isStatic) {
			thisObject = null;
			clazz = method.getDeclaringClass();
		} else {
			clazz = thisObject.getClass();
		}
		try {
			Profiler.start(thisObject, clazz.getName(), method.getName(), args);
			if (isTrace) {
				if (isStatic) {
					Profiler.traceClass(clazz, method.getName());
				} else {
					Profiler.traceObject(thisObject, method.getName());
				}
			}
			Object r = method.invoke(thisObject, args);
			Profiler.returnEnd(thisObject, clazz.getName(), method.getName(),
					args, r);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			Profiler.exceptionEnd(thisObject, clazz.getName(),
					method.getName(), args, t);
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = Helper.makeClassName(classes[i]);
		}
		return StringUtils.join(classNames, ",");
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("invoke");
		argDef.setDef("[-f <filter-expretion>] [-t] <invoke-expretion>");
		argDef.setDesc("invoke a method of the target object.");
		argDef.addOptionDesc(
				"invoke-expretion",
				"like package.ClassName.method(param1,param2) to invoke static method, or method(param1,param2) to invoke method of target object.");
		argDef.addOptionDesc("-t", "to trace the invocation.");
		argDef.addOptionDesc("-f",
				"set <filter-expretion> to filter the invocation output you dont care.");

	}
}
