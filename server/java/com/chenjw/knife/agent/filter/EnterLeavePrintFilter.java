package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodEnterEvent;
import com.chenjw.knife.agent.event.MethodLeaveEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;

/**
 * 当进入和离开方法时打印出相关输出
 * 
 * @author chenjw
 *
 */
public class EnterLeavePrintFilter implements Filter {

	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodEnterEvent) {
			MethodEnterEvent e = (MethodEnterEvent) event;
			MethodStartEvent newEvent = new MethodStartEvent();
			newEvent.setThisObject(e.getThisObject());
			newEvent.setClassName(e.getClassName());
			newEvent.setMethodName(e.getMethodName());
			newEvent.setArguments(e.getArguments());
			newEvent.setFileName(null);
			newEvent.setLineNum(-1);
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
