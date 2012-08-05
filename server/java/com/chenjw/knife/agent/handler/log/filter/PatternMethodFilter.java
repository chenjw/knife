package com.chenjw.knife.agent.handler.log.filter;

import java.util.regex.Pattern;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;

public class PatternMethodFilter implements Filter {
	private Pattern pattern;

	public PatternMethodFilter(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	private boolean isMatch(String className, String methodName) {
		String name = className + "." + methodName;

		if (pattern.matcher(name).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			MethodStartEvent e = (MethodStartEvent) event;
			if (!isMatch(e.getClassName(), e.getMethodName())) {
				return;
			}
		} else if (event instanceof MethodReturnEndEvent) {
			MethodReturnEndEvent e = (MethodReturnEndEvent) event;
			if (!isMatch(e.getClassName(), e.getMethodName())) {
				return;
			}

		} else if (event instanceof MethodExceptionEndEvent) {
			MethodExceptionEndEvent e = (MethodExceptionEndEvent) event;
			if (!isMatch(e.getClassName(), e.getMethodName())) {
				return;
			}
		}
		chain.doFilter(event);
	}
}
