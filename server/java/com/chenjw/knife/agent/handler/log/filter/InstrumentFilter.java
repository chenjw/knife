package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodTraceEvent;
import com.chenjw.knife.agent.service.InstrumentManager;

public class InstrumentFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodTraceEvent) {
			MethodTraceEvent e = (MethodTraceEvent) event;
			InstrumentManager.getInstance().buildTraceMethod(e.getClazz(),
					e.getMethodName());
		} else {
			chain.doFilter(event);
		}
	}
}
