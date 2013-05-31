package com.chenjw.knife.agent.service;

import java.util.Stack;

import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.core.ServiceRegistry;

/**
 * 记录当前对象的变化历史，用来支持“cd ..”这个操作
 * 
 * @author chenjw
 *
 */
public class HistoryService implements Lifecycle {

	private Stack<Integer> history = new Stack<Integer>();

	public Object pre() {
		Stack<Integer> stack = history;
		if (stack.size() <= 1) {
			stack.empty();

			ServiceRegistry.getService(ContextService.class).put(
					Constants.THIS, null);
			return null;
		} else {
			stack.pop();
			Object obj = ServiceRegistry.getService(ObjectHolderService.class)
					.get(stack.peek());
			ServiceRegistry.getService(ContextService.class).put(
					Constants.THIS, obj);
			return obj;
		}
	}

	public Object cd(int id) {
		Object obj = ServiceRegistry.getService(ObjectHolderService.class).get(
				id);
		ServiceRegistry.getService(ContextService.class).put(Constants.THIS,
				obj);
		history.push(id);
		return obj;
	}

	public Object cdObject(Object obj) {
		return cd(ServiceRegistry.getService(ObjectHolderService.class).record(
				obj));
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
