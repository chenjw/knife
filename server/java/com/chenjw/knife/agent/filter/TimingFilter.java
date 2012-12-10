package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;
import com.chenjw.knife.agent.service.TimingService;

public class TimingFilter implements Filter {
	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			int dep = InvokeDepthService.getInstance().getDep();
			TimingService.getInstance().begin(String.valueOf(dep));
			// Agent.println("begin" + String.valueOf(dep));
			chain.doFilter(event);
		} else if (event instanceof MethodReturnEndEvent) {
			try {
				chain.doFilter(event);
			} finally {
				int dep = InvokeDepthService.getInstance().getDep();
				// Agent.println("end" + String.valueOf(dep));
				TimingService.getInstance().end(String.valueOf(dep));
			}

		} else if (event instanceof MethodExceptionEndEvent) {
			try {
				chain.doFilter(event);
			} finally {
				int dep = InvokeDepthService.getInstance().getDep();
				// Agent.println("end" + String.valueOf(dep));
				TimingService.getInstance().end(String.valueOf(dep));
			}
		} else {
			chain.doFilter(event);
		}

	}
}
