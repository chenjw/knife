package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;

/**
 * 用于过滤掉当前方法深度不为0时候的MethodStartEvent、MethodExceptionEndEvent、MethodReturnEndEvent等时间
 * <p>顺序上必须放在DepthFilter之后</p>
 * @author chenjw
 *
 */
public class Depth0Filter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			if (ServiceRegistry.getService(InvokeDepthService.class).getDep() == 0) {
				chain.doFilter(event);
			}
		} else if ((event instanceof MethodReturnEndEvent)
				|| (event instanceof MethodExceptionEndEvent)) {
			if (ServiceRegistry.getService(InvokeDepthService.class).getDep() == 0) {
				chain.doFilter(event);
			}
		}
	}
}