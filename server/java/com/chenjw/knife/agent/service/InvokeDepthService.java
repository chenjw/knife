package com.chenjw.knife.agent.service;

import com.chenjw.knife.agent.core.Lifecycle;

public class InvokeDepthService implements Lifecycle {

	private static final InvokeDepthService INSTANCE = new InvokeDepthService();

	private ThreadLocal<Integer> dep = new ThreadLocal<Integer>();

	public static InvokeDepthService getInstance() {
		return INSTANCE;
	}

	private Integer getStack() {
		Integer stack = dep.get();
		if (stack == null) {
			stack = 0;
			dep.set(stack);
		}
		return stack;
	}

	private void setDep(Integer i) {
		dep.set(i);
	}

	public int getDep() {
		return getStack() - 1;
	}

	public void enter() {
		setDep(getStack() + 1);
	}

	public void leave() {
		setDep(getStack() - 1);
	}

	@Override
	public void init() {

	}

	@Override
	public void clear() {
	}

	@Override
	public void close() {

	}
}
