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
	private static volatile boolean needLogging = true;
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
		trace(dep + 1, field.getClass());
	}

	/**
	 * add trace bytecode
	 * 
	 * @param dep
	 * @param clazz
	 */
	private static void trace(int dep, Class<?> clazz) {

		if (!classSet.contains(clazz)) {
			try {
				classSet.add(clazz);
				// Agent.println("start trace " + clazz);
				InvokeLogUtils.buildTraceClass(dep, clazz);
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

	public static void start(int dep, Object thisObject, String className,
			String methodName, Object[] arguments) {
		if (!needLogging || !isLogThread()) {
			// System.out.println(className + "." + methodName +
			// " start return");
			return;
		}
		needLogging = false;
		// System.out.println(className + "." + methodName +
		// " start return true");
		try {
			InvocationListener listener = getListener();
			listener.onStart(dep, thisObject, className, methodName, arguments);
		} finally {
			needLogging = true;
		}
	}

	public static void returnEnd(int dep, Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		if (!needLogging || !isLogThread()) {
			// System.out.println("returnEnd return");
			return;
		}
		needLogging = false;
		try {
			InvocationListener listener = getListener();
			listener.onReturnEnd(dep, thisObject, className, methodName,
					arguments, result);
			if (result != null) {
				trace(dep, result.getClass());
			}
		} finally {
			needLogging = true;
		}
	}

	private static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return JSON.toJSONString(obj);
		} catch (Throwable e) {
			return obj.toString();
		}
	}

	public static void exceptionEnd(int dep, Object thisObject,
			String className, String methodName, Object[] arguments, Throwable e) {
		if (!needLogging || !isLogThread()) {
			System.out.println("exceptionEnd return");
			return;
		}
		needLogging = false;
		try {
			InvocationListener listener = getListener();
			listener.onExceptionEnd(dep, thisObject, className, methodName,
					arguments, e);
		} finally {
			needLogging = true;
		}
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
			StringBuffer msg = new StringBuffer("[invoke] ");
			msg.append(className + "." + methodName);
			msg.append("(");
			boolean isFirst = true;
			for (Object arg : arguments) {
				if (isFirst) {
					isFirst = false;
				} else {
					msg.append(",");
				}
				msg.append(InvokeLog.toString(arg));
			}
			msg.append(")");
			try {
				Agent.println(d(dep) + msg);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void onReturnEnd(int dep, Object thisObject, String className,
				String methodName, Object[] arguments, Object result) {
			StringBuffer msg = new StringBuffer("[returns] ");
			if (result == null) {
				msg.append("null");
			} else {
				msg.append(JSON.toJSONString(result) + " @"
						+ result.getClass().getName() + "@");
			}
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
