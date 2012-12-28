package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ExceptionInfo implements Serializable {

	private static final long serialVersionUID = 3709168353683115903L;
	private String objectId;
	private String traceString;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getTraceString() {
		return traceString;
	}

	public void setTraceString(String traceString) {
		this.traceString = traceString;
	}

}
