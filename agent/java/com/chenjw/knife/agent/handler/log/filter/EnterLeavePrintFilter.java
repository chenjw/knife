package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodEnterEvent;
import com.chenjw.knife.agent.handler.log.event.MethodLeaveEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodStartEvent;

public class EnterLeavePrintFilter implements Filter {

	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodEnterEvent) {
			MethodEnterEvent e = (MethodEnterEvent) event;
			MethodStartEvent newEvent = new MethodStartEvent();
			newEvent.setThisObject(e.getThisObject());
			newEvent.setClassName(e.getClassName());
			newEvent.setMethodName(e.getMethodName());
			newEvent.setArguments(e.getArguments());
			chain.doFilter(newEvent);
		} else if (event instanceof MethodLeaveEvent) {
			MethodLeaveEvent e = (MethodLeaveEvent) event;
			MethodReturnEndEvent newEvent = new MethodReturnEndEvent();
			newEvent.setThisObject(e.getThisObject());
			newEvent.setClassName(e.getClassName());
			newEvent.setMethodName(e.getMethodName());
			newEvent.setArguments(e.getArguments());
			newEvent.setResult(e.getResult());
			chain.doFilter(newEvent);
		} else {
			chain.doFilter(event);
		}

	}
}
