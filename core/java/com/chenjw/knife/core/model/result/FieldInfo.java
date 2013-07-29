package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class FieldInfo implements Serializable {

	private static final long serialVersionUID = 6957894086001604822L;
	private boolean isStatic;
	private String name;
	private ObjectInfo value;

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectInfo getValue() {
		return value;
	}

	public void setValue(ObjectInfo value) {
		this.value = value;
	}

}
