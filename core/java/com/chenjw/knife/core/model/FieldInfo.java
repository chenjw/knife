package com.chenjw.knife.core.model;

public class FieldInfo {
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
