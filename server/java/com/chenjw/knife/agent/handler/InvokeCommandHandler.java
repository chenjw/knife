package com.chenjw.knife.agent.handler;

import java.lang.reflect.InvocationTargetException;
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
import com.chenjw.knife.agent.handler.log.filter.Depth0Filter;
import com.chenjw.knife.agent.handler.log.filter.DepthFilter;
import com.chenjw.knife.agent.handler.log.filter.ExceptionFilter;
import com.chenjw.knife.agent.handler.log.filter.Filter;
import com.chenjw.knife.agent.handler.log.filter.FixThreadFilter;
import com.chenjw.knife.agent.handler.log.filter.InstrumentFilter;
import com.chenjw.knife.agent.handler.log.filter.InvokeFinishFilter;
import com.chenjw.knife.agent.handler.log.filter.InvokePrintFilter;
import com.chenjw.knife.agent.handler.log.filter.PatternMethodFilter;
import com.chenjw.knife.agent.handler.log.filter.SystemOperationFilter;
import com.chenjw.knife.agent.handler.log.filter.TimingFilter;
import com.chenjw.knife.agent.handler.log.filter.TimingStopFilter;
import com.chenjw.knife.agent.handler.log.listener.FilterInvocationListener;
import com.chenjw.knife.agent.service.ContextManager;
import com.chenjw.knife.agent.util.ParseHelper;
import com.chenjw.knife.agent.util.StringHelper;

public class InvokeCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		boolean isTrace = args.option("-t") != null;
		configStrategy(args);
		invokeMethod(isTrace, args.arg("invoke-expretion"));
	}

	private void configStrategy(Args args) throws Exception {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new FixThreadFilter(Thread.currentThread()));
		filters.add(new ExceptionFilter());
		filters.add(new TimingStopFilter());
		Map<String, String> tOptions = args.option("-t");
		if (tOptions != null) {
			filters.add(new InstrumentFilter());
		}
		Map<String, String> options = args.option("-f");
		if (options != null) {
			filters.add(new PatternMethodFilter(options.get("filter-expretion")));
		}
		filters.add(new DepthFilter());
		if (tOptions == null) {
			filters.add(new Depth0Filter());
		}
		filters.add(new TimingFilter());
		filters.add(new InvokeFinishFilter());
		filters.add(new InvokePrintFilter());
		Profiler.listener = new FilterInvocationListener(filters);
	}

	private void invokeMethod(boolean isTrace, String methodSig)
			throws Exception {

		String argStr = methodSig;
		String m = StringHelper.substringBefore(argStr, "(");
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
					return;
				}
				Method[] methods = clazz.getMethods();
				for (Method tm : methods) {
					if (tm.getName().equals(m)) {
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
					return;
				}
				Method[] methods = obj.getClass().getMethods();
				for (Method tm : methods) {
					if (tm.getName().equals(m)) {
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
				StringHelper.substringBeforeLast(
						StringHelper.substringAfter(argStr, "("), ")"),
				method.getParameterTypes());

		if (Modifier.isStatic(method.getModifiers())) {
			invoke(isTrace, method, null, mArgs);
		} else {
			invoke(isTrace, method,
					ContextManager.getInstance().get(Constants.THIS), mArgs);
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
			Profiler.start(thisObject, clazz.getName(), method.getName(), args,
					null, -1);
			if (isTrace) {
				if (isStatic) {
					Profiler.profileStaticMethod(clazz, method.getName());
				} else {
					Profiler.profileMethod(thisObject, method.getName());
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
			Profiler.exceptionEnd(thisObject, clazz.getName(),
					method.getName(), args, e);
			throw e;
		}
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("invoke");
		argDef.setDef("[-f <filter-expretion>] [-t] <invoke-expretion>");
		argDef.setDesc("invoke a method of the target object.");
		argDef.addOptionDesc(
				"invoke-expretion",
				"input 'package.ClassName.method(param1,param2)' to invoke static method, or 'method(param1,param2)' to invoke the method of target object. The params support json format.");
		argDef.addOptionDesc("-t",
				"is need output the method trace of the invocation..");
		argDef.addOptionDesc("-f",
				"set <filter-expretion> to filter the invocation output you dont care.");

	}
}
