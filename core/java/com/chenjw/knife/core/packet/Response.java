package com.chenjw.knife.core.packet;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 2382539244621556936L;
	private long requestId;
	private Object[] context;

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public Object[] getContext() {
		return context;
	}

	public void setContext(Object[] context) {
		this.context = context;
	}

}