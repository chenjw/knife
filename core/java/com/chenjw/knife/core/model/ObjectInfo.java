package com.chenjw.knife.core.model;

public class ObjectInfo {
	private String objectId;
	private boolean isThrowable;
	private String valueString;

	public boolean isThrowable() {
		return isThrowable;
	}

	public void setThrowable(boolean isThrowable) {
		this.isThrowable = isThrowable;
	}

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
