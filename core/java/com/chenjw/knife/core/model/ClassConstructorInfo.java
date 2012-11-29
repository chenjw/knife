package com.chenjw.knife.core.model;

public class ClassConstructorInfo {
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
