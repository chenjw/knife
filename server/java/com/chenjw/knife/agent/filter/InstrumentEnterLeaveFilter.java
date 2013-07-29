package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.service.InstrumentService;

public class InstrumentEnterLeaveFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEnterLeaveEvent) {
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;

			ServiceRegistry.getService(InstrumentService.class)
					.buildMethodEnterLeave(e.getMethod());
		} else {
			chain.doFilter(event);
		}
	}
}
