package com.chenjw.knife.agent.handler.log;

import java.lang.reflect.Method;

import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodEnterEvent;
import com.chenjw.knife.agent.handler.log.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodLeaveEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodStartEvent;
import com.chenjw.knife.agent.handler.log.event.MethodTraceEvent;

public class Profiler {
	public static final Object VOID = new Object();

	public static volatile ProfilerHandler listener = null;

	public static <T> void traceObject(T obj, String methodName) {
		// System.out.println("proxy " + methodName);
		if (obj == null) {
			return;
		}
		// System.out.println(obj + "." + methodName);
		for (Method method : obj.getClass().getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				traceClass(obj.getClass(), methodName);
			}
		}
		for (Method method : obj.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				traceClass(method.getDeclaringClass(), methodName);
			}
		}
	}

	public static void traceClass(Class<?> clazz, String methodName) {

		if (clazz == null) {
			return;
		}
		trace(clazz, methodName);
	}

	/**
	 * add trace bytecode
	 * 
	 * @param dep
	 * @param clazz
	 */
	private static void trace(final Class<?> clazz, final String methodName) {
		// Agent.println("trace " + clazz + "." + methodName);
		MethodTraceEvent event = new MethodTraceEvent();
		event.setClazz(clazz);
		event.setMethodName(methodName);
		sendEvent(event, null);
	}

	public static void enter(Object thisObject, String className,
			String methodName, Object[] arguments) {
		// Agent.println("enter " + className + " | " + thisObject.getClass()
		// + " | " + methodName);

		MethodEnterEvent event = new MethodEnterEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		sendEvent(event, null);
	}

	public static void leave(Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		MethodLeaveEvent event = new MethodLeaveEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setResult(result);
		sendEvent(event, null);
	}

	public static void start(Object thisObject, String className,
			String methodName, Object[] arguments) {
		// if ("com.chenjw.knife.server.test.impl.CheckServiceImpl".equals(clazz
		// .getName())) {
		if (thisObject != null) {
			// Agent.println(className + " | " + thisObject.getClass());
		}

		// }
		MethodStartEvent event = new MethodStartEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		sendEvent(event, null);
	}

	public static void returnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		MethodReturnEndEvent event = new MethodReturnEndEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setResult(result);
		sendEvent(event, null);
	}

	public static void exceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e) {
		MethodExceptionEndEvent event = new MethodExceptionEndEvent();
		event.setThisObject(thisObject);
		event.setClassName(className);
		event.setMethodName(methodName);
		event.setArguments(arguments);
		event.setE(e);
		sendEvent(event, null);
	}

	private static void sendEvent(Event event, ProfilerCallback callback) {
		if (Profiler.listener != null) {
			try {
				Profiler.listener.onEvent(event, callback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
