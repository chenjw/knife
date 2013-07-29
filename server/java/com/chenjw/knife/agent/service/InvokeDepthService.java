package com.chenjw.knife.agent.service;

import com.chenjw.knife.agent.core.Lifecycle;

/**
 * 记录某一个方法调用的当前设深度（用于invoke或trace方法打印调用栈时确定最前面加多少个"--"））
 * 
 * @author chenjw
 *
 */
public class InvokeDepthService implements Lifecycle {

	private ThreadLocal<Integer> dep = new ThreadLocal<Integer>();

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
