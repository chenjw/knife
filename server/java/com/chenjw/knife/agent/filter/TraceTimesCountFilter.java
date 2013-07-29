package com.chenjw.knife.agent.filter;

import java.util.HashSet;
import java.util.Set;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodEnterEvent;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodLeaveEvent;
import com.chenjw.knife.agent.event.MethodProfileEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;
import com.chenjw.knife.agent.utils.ResultHelper;

/**
 * 用于指定需要监视的方法调用次数，达到次数后会结束监视
 * 
 * @author chenjw
 *
 */
public class TraceTimesCountFilter implements Filter {
	private Set<Thread> threadSet = new HashSet<Thread>();
	private int num;

	public TraceTimesCountFilter(int num) {
		this.num = num;
	}

	private synchronized boolean decrementAndGet() {
		if (num > 0) {
			num--;
			return true;
		} else {
			return false;
		}
	}

	private boolean contains() {
		Thread thread = Thread.currentThread();
		return threadSet.contains(thread);
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodEnterEvent) {
			if (ServiceRegistry.getService(InvokeDepthService.class).getDep() == -1) {
				if (decrementAndGet()) {
					chain.doFilter(event);
					Thread thread = Thread.currentThread();
					threadSet.add(thread);
				}
			}
		} else if (event instanceof MethodLeaveEvent) {
			if (ServiceRegistry.getService(InvokeDepthService.class).getDep() == 0) {
				chain.doFilter(event);
				Thread thread = Thread.currentThread();
				threadSet.remove(thread);
				if (num == 0 && threadSet.size() == 0) {
					// 结束trace命令
					Profiler.listener = null;
					Agent.sendResult(ResultHelper.newResult("trace finished!"));
				}
			}
		} else if (event instanceof MethodStartEvent) {
			if (contains()) {
				chain.doFilter(event);
			}
		} else if (event instanceof MethodReturnEndEvent) {
			if (contains()) {
				chain.doFilter(event);
			}
		} else if (event instanceof MethodExceptionEndEvent) {
			if (contains()) {
				chain.doFilter(event);
			}
		} else if (event instanceof MethodProfileEvent) {
			if (contains()) {
				chain.doFilter(event);
			}
		}

	}

}
