package com.chenjw.knife.core.model;

import java.io.Serializable;

public class MethodExceptionEndInfo implements Serializable {

	private static final long serialVersionUID = -4622693243764283072L;

	private String methodName;

	private ObjectInfo e;
	private long time;

	private int depth;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public ObjectInfo getE() {
		return e;
	}

	public void setE(ObjectInfo e) {
		this.e = e;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
