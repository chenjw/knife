package com.chenjw.knife.agent.handler.log.event;

public class MethodTraceEvent extends Event {
	private Class<?> clazz;
	private String methodName;

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}
