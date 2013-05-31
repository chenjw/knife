package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEnterLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEvent;

/**
 * 设置当前classloader
 * 
 * @author chenjw
 *
 */
public class InstrumentClassLoaderFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEvent) {
			ClassLoader backup = AgentClassLoader.getAgentClassLoader()
					.getParent();
			MethodProfileEvent e = (MethodProfileEvent) event;
			AgentClassLoader.getAgentClassLoader().setParent(
					e.getMethod().getDeclaringClass().getClassLoader());
			try {
				chain.doFilter(event);
			} finally {
				AgentClassLoader.getAgentClassLoader().setParent(backup);
			}
		} else if (event instanceof MethodProfileEnterLeaveEvent) {
			ClassLoader backup = AgentClassLoader.getAgentClassLoader()
					.getParent();
			MethodProfileEnterLeaveEvent e = (MethodProfileEnterLeaveEvent) event;
			AgentClassLoader.getAgentClassLoader().setParent(
					e.getMethod().getDeclaringClass().getClassLoader());
			try {
				chain.doFilter(event);
			} finally {
				AgentClassLoader.getAgentClassLoader().setParent(backup);
			}
		} else {
			chain.doFilter(event);
		}
	}
}
