package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.event.Event;

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
