package com.chenjw.knife.agent.handler.log.listener;

import java.util.List;

import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.ProfilerCallback;
import com.chenjw.knife.agent.handler.log.ProfilerHandler;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.filter.FilterChainImpl;

public class FilterInvocationListener implements ProfilerHandler {
	private List<Filter> filters;

	public FilterInvocationListener(List<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public void onEvent(Event event, ProfilerCallback callback)
			throws Exception {
		FilterChain chain = new FilterChainImpl(filters, callback);
		chain.doFilter(event);
	}

}
