package com.chenjw.knife.agent.service;

import java.util.Stack;

import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.Lifecycle;

public class HistoryService implements Lifecycle {
	private static final HistoryService INSTANCE = new HistoryService();

	private Stack<Integer> history = new Stack<Integer>();

	public static HistoryService getInstance() {
		return INSTANCE;
	}

	public Object pre() {
		Stack<Integer> stack = history;
		if (stack.size() <= 1) {
			stack.empty();
			ContextService.getInstance().put(Constants.THIS, null);
			return null;
		} else {
			stack.pop();
			Object obj = ObjectRecordService.getInstance().get(stack.peek());
			ContextService.getInstance().put(Constants.THIS, obj);
			return obj;
		}
	}

	public Object cd(int id) {
		Object obj = ObjectRecordService.getInstance().get(id);
		ContextService.getInstance().put(Constants.THIS, obj);
		history.push(id);
		return obj;
	}

	public Object cdObject(Object obj) {
		return cd(ObjectRecordService.getInstance().record(obj));
	}

	@Override
	public void init() {

	}

	@Override
	public void clear() {
		history.clear();
	}

	@Override
	public void close() {

	}
}
