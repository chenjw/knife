package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.Profiler;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.service.InvokeDepthManager;

public class InvokeFinishFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			try {
				chain.doFilter(event);
			} finally {
				// Agent.println(InvokeDepth.getDep() + "");
				if (InvokeDepthManager.getInstance().getDep() == 0) {
					Profiler.listener = null;
				}
			}
		} else {
			chain.doFilter(event);
		}

	}

}
