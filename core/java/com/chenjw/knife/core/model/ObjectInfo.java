package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ObjectInfo implements Serializable {

	private static final long serialVersionUID = 4479892158579401619L;
	private String objectId;
	private String valueString;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getValueString() {
		return valueString;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

}
