package com.chenjw.knife.core.model;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = -1029895615419256975L;

	private boolean success = true;

	private String errorMessage;
	private String errorTrace;

	private Object content;

	private String requestId;

	public Result(String requestId) {
		this.requestId = requestId;
	}

	public Result() {

	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorTrace() {
		return errorTrace;
	}

	public void setErrorTrace(String errorTrace) {
		this.errorTrace = errorTrace;
	}

}
