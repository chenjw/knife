package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.event.Event;

public interface Filter {
	public void doFilter(Event event, FilterChain chain) throws Exception;
}
