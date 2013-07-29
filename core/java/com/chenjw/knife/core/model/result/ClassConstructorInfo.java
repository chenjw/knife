package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ClassConstructorInfo implements Serializable {

	private static final long serialVersionUID = -1928961759067639304L;
	private ConstructorInfo[] constructors;
	private String classSimpleName;

	public String getClassSimpleName() {
		return classSimpleName;
	}

	public void setClassSimpleName(String classSimpleName) {
		this.classSimpleName = classSimpleName;
	}

	public ConstructorInfo[] getConstructors() {
		return constructors;
	}

	public void setConstructors(ConstructorInfo[] constructors) {
		this.constructors = constructors;
	}
}
