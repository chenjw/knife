package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class MethodStartInfo implements Serializable {

	private static final long serialVersionUID = -8460894092900733343L;
	private String thisObjectId;
	private String className;
	private String methodName;
	private ObjectInfo[] arguments;
	private String fileName;
	private String lineNum;
	private int depth;

	public String getThisObjectId() {
		return thisObjectId;
	}

	public void setThisObjectId(String thisObjectId) {
		this.thisObjectId = thisObjectId;
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

	public ObjectInfo[] getArguments() {
		return arguments;
	}

	public void setArguments(ObjectInfo[] arguments) {
		this.arguments = arguments;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getLineNum() {
		return lineNum;
	}

	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}

}
