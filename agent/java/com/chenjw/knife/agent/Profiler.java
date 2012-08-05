package com.chenjw.knife.agent;

import java.lang.reflect.Method;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodEnterEvent;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodLeaveEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.event.MethodTraceEvent;

public class Profiler {
	public static final Object VOID = new Object();

	public static volatile ProfilerListener listener = null;

	public static <T> void traceObject(T obj, String methodName) {
		if (Profiler.listener == null) {
			return;
		}
		if (obj == null) {
			return;
		}
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
		if (Profiler.listener == null) {
			return;
		}
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
		MethodTraceEvent event = new MethodTraceEvent();
		event.setClazz(clazz);
		event.setMethodName(methodName);
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
}
