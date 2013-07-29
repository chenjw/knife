package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ResultPart implements Serializable {

	private static final long serialVersionUID = 2515086867227472861L;
	private Object content;

	private String requestId;

	public ResultPart(String requestId) {
		this.requestId = requestId;
	}

	public ResultPart() {

	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
