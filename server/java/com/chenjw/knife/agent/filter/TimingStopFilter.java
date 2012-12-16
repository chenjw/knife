package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.service.TimingService;

public class TimingStopFilter implements Filter {
	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		try {
			ServiceRegistry.getService(TimingService.class).pause();
			chain.doFilter(event);
		} finally {
			ServiceRegistry.getService(TimingService.class).resume();
		}
	}

}
