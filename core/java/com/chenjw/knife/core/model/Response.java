package com.chenjw.knife.core.model;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 2382539244621556936L;
	private String id;
	private String requestId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}