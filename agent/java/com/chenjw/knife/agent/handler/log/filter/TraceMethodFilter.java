package com.chenjw.knife.agent.handler.log.filter;

import java.lang.reflect.Method;

import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodEnterEvent;
import com.chenjw.knife.agent.handler.log.event.MethodLeaveEvent;

public class TraceMethodFilter implements Filter {
	private Class<?> clazz;
	private Method method;
	private Object thisObject;

	public TraceMethodFilter(Object thisObject, Class<?> clazz, Method method) {
		this.thisObject = thisObject;
		this.clazz = clazz;
		this.method = method;
	}

	private boolean isCan(Object thisObject, String className, String methodName) {
		boolean isCan = false;
		// System.out.println(className + "." + methodName);
		if (this.thisObject == null && thisObject == null) {
			if (clazz.getName().equals(className)
					&& method.getName().equals(methodName)) {
				isCan = true;
			}
		} else {
			if (this.thisObject == thisObject) {
				if (clazz.getName().equals(className)
						&& method.getName().equals(methodName)) {
					isCan = true;
				}
			}
		}
		return isCan;
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {

		if (event instanceof MethodEnterEvent) {
			MethodEnterEvent e = (MethodEnterEvent) event;

			if (isCan(e.getThisObject(), e.getClassName(), e.getMethodName())) {
				// System.out.println(e.getClassName());
				chain.doFilter(event);
			}
		} else if (event instanceof MethodLeaveEvent) {
			MethodLeaveEvent e = (MethodLeaveEvent) event;
			if (isCan(e.getThisObject(), e.getClassName(), e.getMethodName())) {
				chain.doFilter(event);
			}
		} else {
			chain.doFilter(event);
		}
	}
}
