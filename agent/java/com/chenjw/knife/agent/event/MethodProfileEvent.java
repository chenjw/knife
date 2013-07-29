package com.chenjw.knife.agent.event;

import java.lang.reflect.Method;

public class MethodProfileEvent extends Event {
	private Object thisObject;
	private Method method;

	public Object getThisObject() {
		return thisObject;
	}

	public void setThisObject(Object thisObject) {
		this.thisObject = thisObject;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
