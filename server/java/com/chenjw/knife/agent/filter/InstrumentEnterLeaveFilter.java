package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.manager.InstrumentManager;

public class InstrumentEnterLeaveFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEnterLeaveEvent) {
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;
			InstrumentManager.getInstance()
					.buildMethodEnterLeave(e.getMethod());
		} else {
			chain.doFilter(event);
		}
	}
}
