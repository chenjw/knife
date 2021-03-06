package com.chenjw.knife.agent.event;

public class MethodStartEvent extends Event {
	private Object thisObject;
	private String className;
	private String methodName;
	private Object[] arguments;
	private String fileName;
	private String lineNum;

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLineNum() {
		return lineNum;
	}

	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}

}
