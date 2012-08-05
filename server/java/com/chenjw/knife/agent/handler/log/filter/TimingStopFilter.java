package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.service.TimingManager;

public class TimingStopFilter implements Filter {
	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		try {
			TimingManager.getInstance().pause();
			chain.doFilter(event);
		} finally {
			TimingManager.getInstance().resume();
		}
	}

}
