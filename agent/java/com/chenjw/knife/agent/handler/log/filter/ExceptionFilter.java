package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.event.Event;

public class ExceptionFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) {
		try {
			chain.doFilter(event);
		} catch (Throwable t) {
			t.printStackTrace();
			Agent.println("exception found, " + t.getClass().getName() + ":"
					+ t.getMessage());
		}
	}

}
