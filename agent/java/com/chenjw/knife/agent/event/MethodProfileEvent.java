package com.chenjw.knife.agent.event;

import java.lang.reflect.Method;

public class MethodProfileEvent extends Event {
	private Method method;

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
