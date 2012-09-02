package com.chenjw.knife.agent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodEnterEvent;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;

public class Profiler {
	public static final String METHOD_NAME_PROFILE_METHOD = "profileMethod";
	public static final String METHOD_NAME_PROFILE_STATIC_METHOD = "profileStaticMethod";
	public static final String METHOD_NAME_ENTER = "enter";
	public static final String METHOD_NAME_LEAVE = "leave";
	public static final String METHOD_NAME_START = "start";
	public static final String METHOD_NAME_RETURN_END = "returnEnd";
	public static final String METHOD_NAME_EXCEPTION_END = "exceptionEnd";

	public static final Object VOID = new Object();

	public static volatile ProfilerListener listener = null;

	public static void profileMethod(Object obj, String className,
			String methodName) {

		if (Profiler.listener == null) {
			return;
		}

		if (obj == null) {
			return;
		}

		for (Method method : findInstanceMethod(obj.getClass(), methodName)) {
			profile(method);
		}
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);

		} catch (ClassNotFoundException e) {

		}
		if (clazz == null) {
			return;
		}
		if (clazz.isInterface()) {
			return;
		}

		for (Method method : findInstanceMethod(clazz, methodName)) {
			profile(method);
		}
	}

	public static void profileStaticMethod(Class<?> clazz, String className,
			String methodName) {

		if (Profiler.listener == null) {
			return;
		}

		if (clazz == null) {
			return;
		}
		for (Method method : findStaticMethod(clazz, methodName)) {
			profile(method);
		}

	}

	// private static void profileEnterLeaveMethod(Object obj, String
	// methodName) {
	// if (Profiler.listener == null) {
	// return;
	// }
	// if (obj == null) {
	// return;
	// }
	// for (Method method : obj.getClass().getDeclaredMethods()) {
	// if (method.getName().equals(methodName)) {
	// profileEnterLeave(obj.getClass(), methodName);
	// }
	// }
	// for (Method method : obj.getClass().getMethods()) {
	// if (method.getName().equals(methodName)) {
	// profileEnterLeave(method.getDeclaringClass(), methodName);
	// }
	// }
	// }
	//
	// private static void profileEnterLeaveStaticMethod(Class<?> clazz,
	// String methodName) {
	// if (Profiler.listener == null) {
	// return;
	// }
	// if (clazz == null) {
	// return;
	// }
	// profileEnterLeave(clazz, methodName);
	// }

	/**
	 * add trace bytecode
	 * 
	 * @param dep
	 * @param clazz
	 */
	public static void profile(Method method) {
		if (Profiler.listener == null) {
			return;
		}
		MethodProfileEvent event = new MethodProfileEvent();
		event.setMethod(method);
		sendEvent(event);
	}

	public static void profileEnterLeave(Method method) {
		if (Profiler.listener == null) {
			return;
		}
		MethodProfileEnterLeaveEvent event = new MethodProfileEnterLeaveEvent();
		event.setMethod(method);

		sendEvent(event);
	}

	public static void enter(Object thisObject, String className,
			String methodName, Object[] arguments) {
		if (Profiler.listener == null) {
			return;
		}
		MethodEnterEvent event = new MethodEnterEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		sendEvent(event);
	}

	public static void leave(Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		if (Profiler.listener == null) {
			return;
		}
		MethodLeaveEvent event = new MethodLeaveEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setResult(result);
		sendEvent(event);
	}

	public static void start(Object thisObject, String className,
			String methodName, Object[] arguments, String fileName, int lineNum) {
		if (Profiler.listener == null) {
			return;
		}
		MethodStartEvent event = new MethodStartEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setFileName(fileName);
		event.setLineNum(lineNum);
		sendEvent(event);
	}

	public static void returnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		if (Profiler.listener == null) {
			return;
		}
		MethodReturnEndEvent event = new MethodReturnEndEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setResult(result);
		sendEvent(event);
	}

	public static void exceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e) {
		if (Profiler.listener == null) {
			return;
		}
		MethodExceptionEndEvent event = new MethodExceptionEndEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setE(e);
		sendEvent(event);
	}

	private static void sendEvent(Event event) {
		if (Profiler.listener != null) {
			try {
				Profiler.listener.onEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Set<Method> findStaticMethod(Class<?> clazz, String methodName) {
		Set<Method> methods = new HashSet<Method>();

		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				if (Modifier.isStatic(method.getModifiers())) {
					methods.add(method);
				}
			}
		}
		return methods;
	}

	public static Set<Method> findInstanceMethod(Class<?> clazz,
			String methodName) {
		Set<Method> methods = new HashSet<Method>();

		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				if (!Modifier.isStatic(method.getModifiers())) {
					methods.add(method);
				}
			}
		}
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				if (!Modifier.isStatic(method.getModifiers())) {
					methods.add(method);
				}
			}
		}

		return methods;
	}
}
