package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthManager;

public class DepthFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			InvokeDepthManager.getInstance().enter();
			chain.doFilter(event);
		} else if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			try {
				chain.doFilter(event);
			} finally {
				InvokeDepthManager.getInstance().leave();
			}

		} else {
			chain.doFilter(event);
		}

	}
}
