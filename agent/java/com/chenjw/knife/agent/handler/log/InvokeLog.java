package com.chenjw.knife.agent.handler.log;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.set.SynchronizedSet;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.handler.log.listener.DefaultInvocationListener;

public class InvokeLog {
	private static final InvocationListener DEFAULT_LISTENER = new DefaultInvocationListener();
	public static InvocationListener listener = null;
	public static Thread checkThread = null;
	private static volatile boolean needLogging = true;

	@SuppressWarnings("unchecked")
	private static Set<Class<?>> classSet = SynchronizedSet
			.decorate(new HashSet<Class<?>>());

	public static void clear() {
		Agent.clear();
		classSet.clear();
	}

	public static <T> void traceObject(T obj) {
		if (obj == null) {
			return;
		}
		traceClass(obj.getClass());
	}

	public static void traceClass(Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		// Agent.println("proxy " + clazz.getName());
		trace(clazz);
	}

	/**
	 * add trace bytecode
	 * 
	 * @param dep
	 * @param clazz
	 */
	private static void trace(Class<?> clazz) {
		if (!needLogging) {
			return;
		}
		if (!classSet.contains(clazz)) {
			try {
				classSet.add(clazz);
				// Agent.println("start trace " + clazz);
				InvokeLogUtils.buildTraceClass(clazz);
				// Agent.println("end trace " + clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isLogThread() {
		if (checkThread == null) {
			return true;
		} else {
			if (checkThread == Thread.currentThread()) {
				return true;
			}
		}
		return false;
	}

	private static InvocationListener getListener() {
		InvocationListener listener = null;
		if (InvokeLog.listener != null) {
			listener = InvokeLog.listener;
		} else {
			listener = InvokeLog.DEFAULT_LISTENER;
		}
		return listener;
	}

	public static void start(Object thisObject, String className,
			String methodName, Object[] arguments) {
		if (!needLogging || !isLogThread()) {
			return;
		}

		needLogging = false;
		// System.out.println(className + "." + methodName +
		// " start return true");
		try {
			InvocationListener listener = getListener();
			listener.onStart(thisObject, className, methodName, arguments);
		} finally {
			needLogging = true;
		}
	}

	public static void returnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		if (!needLogging || !isLogThread()) {
			return;
		}
		needLogging = false;
		try {
			InvocationListener listener = getListener();
			listener.onReturnEnd(thisObject, className, methodName, arguments,
					result);
			if (result != null) {
				trace(result.getClass());
			}
		} finally {
			needLogging = true;
		}
	}

	public static void exceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e) {
		if (!needLogging || !isLogThread()) {
			return;
		}

		needLogging = false;
		try {
			InvocationListener listener = getListener();
			listener.onExceptionEnd(thisObject, className, methodName,
					arguments, e);
		} finally {
			needLogging = true;

		}
	}

}
