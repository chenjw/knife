package com.chenjw.knife.agent.handler.log.filter;

import java.util.List;

import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.ProfilerCallback;
import com.chenjw.knife.agent.handler.log.event.Event;

public class FilterChainImpl implements FilterChain {
	private List<Filter> filters;
	private int index;
	private ProfilerCallback callback;

	public FilterChainImpl(List<Filter> filters, ProfilerCallback callback) {
		this.filters = filters;
		this.index = 0;
		this.callback = callback;
	}

	@Override
	public void doFilter(Event event) throws Exception {
		if (filters == null) {
			return;
		} else if (filters.size() <= index) {
			if (callback != null) {
				callback.doCallback();
			}
		} else {
			Filter f = filters.get(index);
			this.index++;
			f.doFilter(event, this);
		}
	}
}
