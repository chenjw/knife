package com.chenjw.knife.core.result;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = -1029895615419256975L;

	private boolean isSuccess;

	private String errorMessage;
	private String errorTrace;

	private Object content;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
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
