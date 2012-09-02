package com.chenjw.knife.agent.filter;

import java.util.List;

import com.chenjw.knife.agent.ProfilerListener;
import com.chenjw.knife.agent.event.Event;

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
