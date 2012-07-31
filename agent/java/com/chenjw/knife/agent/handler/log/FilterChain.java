package com.chenjw.knife.agent.handler.log;

import com.chenjw.knife.agent.handler.log.event.Event;

public interface FilterChain {
	public void doFilter(Event event) throws Exception;
}
