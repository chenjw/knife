package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.event.Event;

/**
 * 更新当前线程的contextClassLoader为agentClassLoader，因为线程执行时某些三方库（如fastjson）
 * 会使用contextClassLoader加载class
 * 
 * @author chenjw
 * 
 */
public class CurrentContextClassLoaderFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		Thread currentThread = Thread.currentThread();
		ClassLoader currentClassLoader = currentThread.getContextClassLoader();
		currentThread.setContextClassLoader(AgentClassLoader
				.getAgentClassLoader());
		try {
			chain.doFilter(event);
		} finally {
			currentThread.setContextClassLoader(currentClassLoader);
		}
	}
}
