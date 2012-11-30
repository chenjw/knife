package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ReferenceCountInfo implements Serializable {

	private static final long serialVersionUID = -9156653940303404294L;
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
