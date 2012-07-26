package com.chenjw.knife.agent.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javassist.Modifier;

import org.apache.commons.lang.StringUtils;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.ParseHelper;
import com.chenjw.knife.agent.handler.log.Profiler;
import com.chenjw.knife.agent.handler.log.filter.PatternMethodFilter;
import com.chenjw.knife.agent.handler.log.listener.DefaultInvocationListener;

public class InvokeCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			addLogger(args);
			invokeMethod(args.getArgStr());
		} catch (Exception e) {
			e.printStackTrace();
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		} finally {
			clearLogger();
		}
	}

	private void addLogger(Args args) throws Exception {

		Object thisObject = Context.get(Constants.THIS);
		if (thisObject != null) {
			Profiler.checkThread = Thread.currentThread();
		}
		String[] arg = args.arg("-f");
		if (arg != null) {
			DefaultInvocationListener listener = new DefaultInvocationListener();
			listener.setMethodFilter(new PatternMethodFilter(arg[0]));
			Profiler.listener = listener;
		}

	}

	private void clearLogger() {
		Profiler.clear();
		Profiler.checkThread = null;
		Profiler.listener = null;
	}

	private void invokeMethod(String methodSig)
			throws IllegalArgumentException, IllegalAccessException {

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
			invoke(method, null, mArgs);
		} else {
			invoke(method, Context.get(Constants.THIS), mArgs);
		}
	}

	private void invoke(Method method, Object thisObject, Object[] args)
			throws IllegalArgumentException, IllegalAccessException {
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
			if (isStatic) {
				Profiler.traceClass(clazz, method.getName());
			} else {
				Profiler.traceObject(thisObject, method.getName());
			}
			Object r = method.invoke(thisObject, args);
			Profiler.returnEnd(thisObject, clazz.getName(), method.getName(),
					args, r);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			Profiler.exceptionEnd(thisObject, clazz.getName(),
					method.getName(), args, t);
		} catch (Exception e) {

		}
	}

	public static String getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = Helper.makeClassName(classes[i]);
		}
		return StringUtils.join(classNames, ",");
	}

	@Override
	public String getName() {
		return "invoke";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {
		argDecls.put("-f", 1);
	}
}
