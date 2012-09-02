package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;

public interface Filter {
	public void doFilter(Event event, FilterChain chain) throws Exception;
}
