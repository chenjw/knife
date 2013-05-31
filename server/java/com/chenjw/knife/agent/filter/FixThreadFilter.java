package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;

/**
 * 过滤到非当前线程产生的事件
 * 
 * @author chenjw
 *
 */
public class FixThreadFilter implements Filter {
	private volatile Thread checkThread = null;

	public FixThreadFilter(Thread checkThread) {
		this.checkThread = checkThread;
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (Thread.currentThread() == checkThread) {
			chain.doFilter(event);
		}
	}

}
