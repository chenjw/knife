package com.chenjw.knife.agent.manager;

import java.util.Stack;

import com.chenjw.knife.agent.constants.Constants;

public class HistoryManager implements Lifecycle {
	private static final HistoryManager INSTANCE = new HistoryManager();

	private Stack<Integer> history = new Stack<Integer>();

	public static HistoryManager getInstance() {
		return INSTANCE;
	}

	public Object pre() {
		Stack<Integer> stack = history;
		if (stack.size() <= 1) {
			stack.empty();
			ContextManager.getInstance().put(Constants.THIS, null);
			return null;
		} else {
			stack.pop();
			Object obj = ObjectRecordManager.getInstance().get(stack.peek());
			ContextManager.getInstance().put(Constants.THIS, obj);
			return obj;
		}
	}

	public Object cd(int id) {
		Object obj = ObjectRecordManager.getInstance().get(id);
		ContextManager.getInstance().put(Constants.THIS, obj);
		history.push(id);
		return obj;
	}

	public Object cdObject(Object obj) {
		return cd(ObjectRecordManager.getInstance().record(obj));
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		history.clear();
	}

	@Override
	public void close() {

	}
}
