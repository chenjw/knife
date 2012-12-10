package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEvent;
import com.chenjw.knife.agent.service.InstrumentService;

public class InstrumentFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEvent) {
			MethodProfileEvent e = (MethodProfileEvent) event;
			InstrumentService.getInstance().buildTraceMethod(e.getMethod());
			chain.doFilter(event);
		} else if (event instanceof MethodProfileEnterLeaveEvent) {
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;
			InstrumentService.getInstance().buildTraceMethod(e.getMethod());
			chain.doFilter(event);
		} else {
			chain.doFilter(event);
		}
	}
}
