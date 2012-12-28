package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ClassFieldInfo implements Serializable {
	private static final long serialVersionUID = 8586002246962463843L;
	private FieldInfo[] fields;

	public FieldInfo[] getFields() {
		return fields;
	}

	public void setFields(FieldInfo[] fields) {
		this.fields = fields;
	}

}
