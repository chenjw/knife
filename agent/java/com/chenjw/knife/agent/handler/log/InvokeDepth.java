package com.chenjw.knife.agent.handler.log;

public class InvokeDepth {
	private static ThreadLocal<Integer> dep = new ThreadLocal<Integer>();

	private static Integer getStack() {
		Integer stack = dep.get();
		if (stack == null) {
			stack = 0;
			dep.set(stack);
		}
		return stack;
	}

	private static void setDep(Integer i) {
		dep.set(i);
	}

	public static int getDep() {
		return getStack() - 1;
	}

	public static void enter() {

		setDep(getStack() + 1);
	}

	public static void leave() {
		setDep(getStack() - 1);
	}
}
