package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class MethodInfo implements Serializable {

	private static final long serialVersionUID = 6024946875563214231L;
	private boolean isStatic;
	private String name;

	private String[] paramClassNames;

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

	public String[] getParamClassNames() {
		return paramClassNames;
	}

	public void setParamClassNames(String[] paramClassNames) {
		this.paramClassNames = paramClassNames;
	}

}
