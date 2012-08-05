package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.event.Event;

public interface FilterChain {
	public void doFilter(Event event) throws Exception;
}
