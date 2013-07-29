package com.chenjw.knife.agent.filter;

import java.util.HashSet;
import java.util.Set;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodProfileEvent;

/**
 * 对指定对象不进行跟踪的过滤器
 * 
 * @author chenjw
 * 
 */
public class StopObjectsFilter implements Filter {
	private Set<Object> objectSet = new HashSet<Object>();

	public StopObjectsFilter(Object[] objs) {
		if (objs != null) {
			for (Object obj : objs) {
				objectSet.add(obj);
			}
		}
	}

	private boolean isCan(Object o) {
		if (o == null) {
			return true;
		}
		return !objectSet.contains(o);
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodProfileEvent) {
			MethodProfileEvent e = (MethodProfileEvent) event;
			if (isCan(e.getThisObject())) {
				chain.doFilter(event);
			}

		} else {
			chain.doFilter(event);
		}
	}
}
