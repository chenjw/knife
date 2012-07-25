package com.chenjw.knife.agent.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
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
		Object thisObject = Context.get(Constants.THIS);
		if (thisObject == null) {
			Agent.println("current object not found!");
		}
		String argStr = methodSig;
		String m = StringUtils.substringBefore(argStr, "(");
		m = m.trim();
		Method method = null;
		if (StringUtils.isNumeric(m)) {
			method = ((Method[]) Context.get(Constants.METHOD_LIST))[Integer
					.parseInt(m)];
		} else {
			Object obj = Context.get(Constants.THIS);
			if (obj != null) {
				Method[] methods = obj.getClass().getMethods();
				for (Method tm : methods) {
					if (StringUtils.equals(tm.getName(), m)) {
						method = tm;
						continue;
					}
				}
			}
		}
		if (method == null) {
			Agent.println("cant find method!");
			return;
		}
		Object[] mArgs = getMethodArgs(
				method.getParameterTypes(),
				StringUtils.substringBeforeLast(
						StringUtils.substringAfter(argStr, "("), ")"));
		invoke(method, thisObject, mArgs);
	}

	private void invoke(Method method, Object thisObject, Object[] args)
			throws IllegalArgumentException, IllegalAccessException {
		try {
			Profiler.start(thisObject, thisObject.getClass().getName(),
					method.getName(), args);
			Profiler.traceObject(thisObject, method.getName());
			Object r = method.invoke(thisObject, args);
			Profiler.returnEnd(thisObject, thisObject.getClass().getName(),
					method.getName(), args, r);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			Profiler.exceptionEnd(thisObject, thisObject.getClass().getName(),
					method.getName(), args, t);
		} catch (Exception e) {

		}
	}

	private Object[] getMethodArgs(Class<?>[] types, String str) {
		Object[] objs = new Object[types.length];
		int i = 0;
		for (Class<?> type : types) {
			int end = getFirstArgIndex(str);
			String s = StringUtils.substring(str, 0, end);
			objs[i] = JSON.parseObject(s, type);
			str = StringUtils.substring(str, end + 1);
			i++;
		}
		return objs;
	}

	private int getFirstArgIndex(String str) {
		int n = 0;
		int i = 0;
		for (char c : str.toCharArray()) {
			if (c == '(') {
				n++;
			} else if (c == ')') {
				n--;
			} else if (c == '[') {
				n++;
			} else if (c == ']') {
				n--;
			} else if (c == ',') {
				if (n == 0) {
					return i;
				}
			}
			i++;
		}
		return i;
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
