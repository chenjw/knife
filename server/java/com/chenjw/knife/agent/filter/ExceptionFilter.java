package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.event.Event;

public class ExceptionFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) {
		try {
			chain.doFilter(event);
		} catch (Throwable t) {
			Agent.info("exception found, " + t.getClass().getName() + ":"
					+ t.getMessage());
			Agent.print(t);
		}
	}

}
