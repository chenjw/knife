package com.chenjw.knife.agent.filter;

import java.util.List;

import com.chenjw.knife.agent.ProfilerListener;
import com.chenjw.knife.agent.event.Event;

/**
 * 用于当监听到事件时可以通过指定的过滤器链
 * 
 * @author chenjw
 *
 */
public class FilterInvocationListener implements ProfilerListener {
	private List<Filter> filters;

	public FilterInvocationListener(List<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		FilterChain chain = new FilterChainImpl(filters);
		chain.doFilter(event);
	}

}
