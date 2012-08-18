package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.service.InstrumentManager;

public class InstrumentEnterLeaveFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEnterLeaveEvent) {
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;
			InstrumentManager.getInstance().buildEnterLeaveMethod(e.getClazz(),
					e.getMethodName());
		} else {
			chain.doFilter(event);
		}
	}
}
