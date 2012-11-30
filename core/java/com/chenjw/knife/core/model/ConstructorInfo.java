package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ConstructorInfo implements Serializable {

	private static final long serialVersionUID = 1709954392317197243L;
	private String[] paramClassNames;

	public String[] getParamClassNames() {
		return paramClassNames;
	}

	public void setParamClassNames(String[] paramClassNames) {
		this.paramClassNames = paramClassNames;
	}

}
