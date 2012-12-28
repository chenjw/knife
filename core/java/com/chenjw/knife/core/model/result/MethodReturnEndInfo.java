package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class MethodReturnEndInfo implements Serializable {

	private static final long serialVersionUID = -9144151585455853112L;

	private boolean isVoid;

	private ObjectInfo result;

	private int depth;
	private long time;

	public boolean isVoid() {
		return isVoid;
	}

	public void setVoid(boolean isVoid) {
		this.isVoid = isVoid;
	}

	public ObjectInfo getResult() {
		return result;
	}

	public void setResult(ObjectInfo result) {
		this.result = result;
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
