package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.InvokeDepth;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodStartEvent;

public class Depth0Filter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			if (InvokeDepth.getDep() == 0) {
				chain.doFilter(event);
			}
		} else if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			if (InvokeDepth.getDep() == 0) {
				chain.doFilter(event);
			}
		}
	}
}