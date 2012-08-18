package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEvent;
import com.chenjw.knife.agent.service.InstrumentManager;

public class InstrumentFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEvent) {
			MethodProfileEvent e = (MethodProfileEvent) event;
			InstrumentManager.getInstance().buildTraceMethod(e.getClazz(),
					e.getMethodName());
			chain.doFilter(event);
		} else if (event instanceof MethodProfileEnterLeaveEvent) {
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;
			InstrumentManager.getInstance().buildTraceMethod(e.getClazz(),
					e.getMethodName());
			chain.doFilter(event);
		} else {
			chain.doFilter(event);
		}
	}
}
