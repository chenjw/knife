package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;
import com.chenjw.knife.agent.service.TimingService;

/**
 * 记录调用时间
 * 
 * @author chenjw
 *
 */
public class TimingFilter implements Filter {
	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			int dep = ServiceRegistry.getService(InvokeDepthService.class)
					.getDep();
			ServiceRegistry.getService(TimingService.class).begin(
					String.valueOf(dep));
			// Agent.println("begin" + String.valueOf(dep));
			chain.doFilter(event);
		} else if (event instanceof MethodReturnEndEvent) {
			try {
				chain.doFilter(event);
			} finally {
				int dep = ServiceRegistry.getService(InvokeDepthService.class)
						.getDep();
				// Agent.println("end" + String.valueOf(dep));
				ServiceRegistry.getService(TimingService.class).end(
						String.valueOf(dep));
			}

		} else if (event instanceof MethodExceptionEndEvent) {
			try {
				chain.doFilter(event);
			} finally {
				int dep = ServiceRegistry.getService(InvokeDepthService.class)
						.getDep();
				// Agent.println("end" + String.valueOf(dep));
				ServiceRegistry.getService(TimingService.class).end(
						String.valueOf(dep));
			}
		} else {
			chain.doFilter(event);
		}

	}
}
