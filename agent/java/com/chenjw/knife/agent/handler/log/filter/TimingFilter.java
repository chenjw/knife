package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthManager;
import com.chenjw.knife.agent.service.TimingManager;

public class TimingFilter implements Filter {
	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			int dep = InvokeDepthManager.getInstance().getDep();
			TimingManager.getInstance().begin(String.valueOf(dep));
			// Agent.println("begin" + String.valueOf(dep));
			chain.doFilter(event);
		} else if (event instanceof MethodReturnEndEvent) {
			try {
				chain.doFilter(event);
			} finally {
				int dep = InvokeDepthManager.getInstance().getDep();
				// Agent.println("end" + String.valueOf(dep));
				TimingManager.getInstance().end(String.valueOf(dep));
			}

		} else if (event instanceof MethodExceptionEndEvent) {
			try {
				chain.doFilter(event);
			} finally {
				int dep = InvokeDepthManager.getInstance().getDep();
				// Agent.println("end" + String.valueOf(dep));
				TimingManager.getInstance().end(String.valueOf(dep));
			}
		} else {
			chain.doFilter(event);
		}

	}
}
