package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;

public interface FilterChain {
	public void doFilter(Event event) throws Exception;
}
