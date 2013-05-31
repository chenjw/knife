package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.utils.ResultHelper;

/**
 * 发生异常时打印到client
 * 
 * @author chenjw
 *
 */
public class ExceptionFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) {
		try {
			chain.doFilter(event);
		} catch (Throwable t) {
			Agent.sendResult(ResultHelper.newErrorResult("exception found, "
					+ t.getClass().getName() + ":" + t.getMessage(), t));
		}
	}



}
