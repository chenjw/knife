package com.chenjw.knife.agent.handler.log;

import java.util.Stack;

public class InvokeDepth {
	private static ThreadLocal<Stack<Object>> dep = new ThreadLocal<Stack<Object>>();

	private static Stack<Object> getStack() {
		Stack<Object> stack = dep.get();
		if (stack == null) {
			stack = new Stack<Object>();
			dep.set(stack);
		}
		return stack;
	}

	public static int getDep() {
		Stack<Object> stack = getStack();
		return stack.size() - 1;
	}

	public static void enter(Object obj) {

		Stack<Object> stack = getStack();
		// if (stack.size() == 0 || stack.peek() != obj) {
		// Agent.println("enter" + obj.toString());
		stack.push(obj);
		// }
	}

	public static void leave(Object obj) {
		Stack<Object> stack = getStack();
		// if (stack.size() != 0 && stack.peek() == obj) {
		// Agent.println("leave" + obj.toString());
		stack.pop();
		// }
	}
}
