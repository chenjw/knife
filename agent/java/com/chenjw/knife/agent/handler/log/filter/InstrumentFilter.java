package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.TraceCodeBuilder;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodTraceEvent;

public class InstrumentFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodTraceEvent) {
			MethodTraceEvent e = (MethodTraceEvent) event;
			TraceCodeBuilder.buildTraceMethod(e.getClazz(), e.getMethodName());
		} else {
			chain.doFilter(event);
		}
	}
}
