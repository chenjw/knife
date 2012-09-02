package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.manager.InvokeDepthManager;

public class Depth0Filter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			if (InvokeDepthManager.getInstance().getDep() == 0) {
				chain.doFilter(event);
			}
		} else if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			if (InvokeDepthManager.getInstance().getDep() == 0) {
				chain.doFilter(event);
			}
		}
	}
}