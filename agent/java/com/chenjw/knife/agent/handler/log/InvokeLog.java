package com.chenjw.knife.agent.handler.log;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.set.SynchronizedSet;

import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Agent;

public class InvokeLog {
	private static final InvocationListener DEFAULT_LISTENER = new PrintInvocationListener();
	public static InvocationListener listener = null;
	public static Thread checkThread = null;

	@SuppressWarnings("unchecked")
	private static Set<Class<?>> classSet = SynchronizedSet
			.decorate(new HashSet<Class<?>>());

	public static void clear() {
		Agent.clear();
		classSet.clear();
	}

	public static <T> void proxy(int dep, T field) {

		if (field == null) {
			return;
		}
		// System.out.println("start proxy " + field.getClass());
		Class<?> clazz = field.getClass();
		if (!classSet.contains(clazz)) {
			try {
				classSet.add(clazz);
				InvokeLogUtils.buildTraceClass(dep + 1, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println("end proxy " + field.getClass());
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

	public static void start(int dep, Object thisObject, String className,
			String methodName, Object[] arguments) {
		if (!isLogThread()) {
			return;
		}
		InvocationListener listener = getListener();
		listener.onStart(dep, thisObject, className, methodName, arguments);
	}

	public static void returnEnd(int dep, Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		if (!isLogThread()) {
			return;
		}
		InvocationListener listener = getListener();
		listener.onReturnEnd(dep, thisObject, className, methodName, arguments,
				result);
	}

	public static void exceptionEnd(int dep, Object thisObject,
			String className, String methodName, Object[] arguments, Throwable e) {
		if (!isLogThread()) {
			return;
		}
		InvocationListener listener = getListener();
		listener.onExceptionEnd(dep, thisObject, className, methodName,
				arguments, e);
	}

	private static class PrintInvocationListener implements InvocationListener {
		private String d(int dep) {
			String s = "";
			for (int i = 0; i < dep; i++) {
				s += "----";
			}
			return s;
		}

		@Override
		public void onStart(int dep, Object thisObject, String className,
				String methodName, Object[] arguments) {
			String msg = null;
			msg = className + "." + methodName;

			try {
				Agent.println(d(dep) + msg);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void onReturnEnd(int dep, Object thisObject, String className,
				String methodName, Object[] arguments, Object result) {
			String msg = null;
			msg = "[returns] " + JSON.toJSONString(result);

			try {
				Agent.println(d(dep) + msg);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		@Override
		public void onExceptionEnd(int dep, Object thisObject,
				String className, String methodName, Object[] arguments,
				Throwable e) {
			String msg = null;
			msg = "[throws] " + e;
			try {
				Agent.println(d(dep) + msg);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

}
