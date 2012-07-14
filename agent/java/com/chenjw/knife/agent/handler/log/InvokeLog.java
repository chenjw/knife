package com.chenjw.knife.agent.handler.log;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.set.SynchronizedSet;

import com.chenjw.knife.agent.Agent;

public class InvokeLog {
	public static void init() {

	}

	@SuppressWarnings("unchecked")
	private static Set<Class<?>> classSet = SynchronizedSet
			.decorate(new HashSet<Class<?>>());

	public static void clear() {
		Agent.clear();
		classSet.clear();
	}

	public static <T> void proxy(T field) {

		if (field == null) {
			return;
		}
		// System.out.println("start proxy " + field.getClass());
		Class<?> clazz = field.getClass();
		if (!classSet.contains(clazz)) {
			try {
				classSet.add(clazz);
				InvokeLogUtils.buildMockClass(clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println("end proxy " + field.getClass());
	}

	public static void logInvoke(String className, String methodName,
			Object[] arguments, Object result, Throwable e) {
		String msg = null;
		if (e != null) {
			msg = className + "." + methodName + " throws " + e;
		} else {
			msg = className + "." + methodName + " returns " + result;
		}
		try {
			Agent.print(msg);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
