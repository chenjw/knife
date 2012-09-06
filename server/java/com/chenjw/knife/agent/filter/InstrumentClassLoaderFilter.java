package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEvent;

public class InstrumentClassLoaderFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEvent) {
			MethodProfileEvent e = (MethodProfileEvent) event;
			AgentClassLoader.getAgentClassLoader().setParent(
					e.getMethod().getDeclaringClass().getClassLoader());
			chain.doFilter(event);
		} else if (event instanceof MethodProfileEnterLeaveEvent) {
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;
			AgentClassLoader.getAgentClassLoader().setParent(
					e.getMethod().getDeclaringClass().getClassLoader());
			chain.doFilter(event);
		} else {
			chain.doFilter(event);
		}
	}
}
