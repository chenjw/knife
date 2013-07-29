package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;

/**
 * 用于记录当前方法帧的深度
 * 
 * @author chenjw
 *
 */
public class DepthFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			ServiceRegistry.getService(InvokeDepthService.class).enter();
			chain.doFilter(event);
		} else if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			try {
				chain.doFilter(event);
			} finally {
				ServiceRegistry.getService(InvokeDepthService.class).leave();
			}

		} else {
			chain.doFilter(event);
		}

	}
}
