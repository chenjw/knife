package com.chenjw.knife.agent.filter;

import java.util.regex.Pattern;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;

/**
 * 根据正则表达式过滤掉不需要了解的调用细节
 * 
 * @author chenjw
 *
 */
public class PatternMethodFilter implements Filter {
	private Pattern pattern;

	public PatternMethodFilter(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	private boolean isMatch(Object thisObject,String className, String methodName) {
		String cName;
		if (thisObject != null) {
			cName=thisObject.getClass().getName();
		} else {
			cName=className;
		}
		String name = cName + "." + methodName;

		if (pattern.matcher(name).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			MethodStartEvent e = (MethodStartEvent) event;
			if (!isMatch(e.getThisObject(),e.getClassName(), e.getMethodName())) {
				return;
			}
		} else if (event instanceof MethodReturnEndEvent) {
			MethodReturnEndEvent e = (MethodReturnEndEvent) event;
			if (!isMatch(e.getThisObject(),e.getClassName(), e.getMethodName())) {
				return;
			}

		} else if (event instanceof MethodExceptionEndEvent) {
			MethodExceptionEndEvent e = (MethodExceptionEndEvent) event;
			if (!isMatch(e.getThisObject(),e.getClassName(), e.getMethodName())) {
				return;
			}
		}
		chain.doFilter(event);
	}
}
