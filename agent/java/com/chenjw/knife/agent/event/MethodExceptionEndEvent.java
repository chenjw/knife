package com.chenjw.knife.agent.event;

public class MethodExceptionEndEvent extends Event {
	private Object thisObject;
	private String className;
	private String methodName;
	private Object[] arguments;
	private Throwable e;

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

	public Throwable getE() {
		return e;
	}

	public void setE(Throwable e) {
		this.e = e;
	}

}
