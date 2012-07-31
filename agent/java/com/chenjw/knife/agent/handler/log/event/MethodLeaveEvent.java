package com.chenjw.knife.agent.handler.log.event;

public class MethodLeaveEvent extends Event {
	private Object thisObject;
	private String className;
	private String methodName;
	private Object[] arguments;
	private Object result;

	public Object getThisObject() {
		return thisObject;
	}

	public void setThisObject(Object thisObject) {
		this.thisObject = thisObject;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
