package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;

/**
 * 过滤器链
 * 
 * @author chenjw
 *
 */
public interface FilterChain {
	public void doFilter(Event event) throws Exception;
}
