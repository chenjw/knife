package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.utils.TimingHelper;

public class TimingFilter implements Filter {
	private static void stopTiming() {
		TimingHelper.stop();
	}

	private static void resumeTiming() {
		TimingHelper.resume();
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		try {
			stopTiming();
			chain.doFilter(event);
		} finally {
			resumeTiming();
		}
	}
}
