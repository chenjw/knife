package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.service.TimingService;

public class TimingStopFilter implements Filter {
	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		try {
			TimingService.getInstance().pause();
			chain.doFilter(event);
		} finally {
			TimingService.getInstance().resume();
		}
	}

}
