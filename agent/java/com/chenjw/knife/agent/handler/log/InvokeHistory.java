package com.chenjw.knife.agent.handler.log;

import java.util.Stack;

import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.handler.constants.Constants;

public class InvokeHistory {
	private static Stack<Integer> history = new Stack<Integer>();

	public static Object pre() {
		Stack<Integer> stack = history;
		if (stack.size() <= 1) {
			stack.empty();
			Context.put(Constants.THIS, null);
			return null;
		} else {
			stack.pop();
			Object obj = InvokeRecord.get(stack.peek());
			Context.put(Constants.THIS, obj);
			return obj;
		}
	}

	public static Object cd(int id) {
		Object obj = InvokeRecord.get(id);
		Context.put(Constants.THIS, obj);
		history.push(id);
		return obj;
	}

	public static Object cdObject(Object obj) {
		return cd(InvokeRecord.record(obj));
	}
}
