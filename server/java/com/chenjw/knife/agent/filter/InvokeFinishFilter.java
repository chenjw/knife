package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;

public class InvokeFinishFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			try {
				chain.doFilter(event);
			} finally {
				// Agent.println(InvokeDepth.getDep() + "");
				if (InvokeDepthService.getInstance().getDep() == 0) {
					Profiler.listener = null;
				}
			}
		} else {
			chain.doFilter(event);
		}

	}

}
