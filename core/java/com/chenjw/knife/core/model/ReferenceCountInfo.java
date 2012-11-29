package com.chenjw.knife.core.model;

public class ReferenceCountInfo {

	private ObjectInfo obj;
	private long count;

	public ObjectInfo getObj() {
		return obj;
	}

	public void setObj(ObjectInfo obj) {
		this.obj = obj;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
