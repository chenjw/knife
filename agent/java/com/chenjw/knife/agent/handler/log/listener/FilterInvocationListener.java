package com.chenjw.knife.agent.handler.log.listener;

import java.util.List;

import com.chenjw.knife.agent.handler.log.ProfilerListener;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.filter.Filter;
import com.chenjw.knife.agent.handler.log.filter.FilterChain;
import com.chenjw.knife.agent.handler.log.filter.FilterChainImpl;

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
