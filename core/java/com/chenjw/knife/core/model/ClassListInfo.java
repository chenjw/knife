package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ClassListInfo implements Serializable {

	private static final long serialVersionUID = 7855978950886901067L;
	private String expression;
	private ClassInfo[] classes;

	public ClassInfo[] getClasses() {
		return classes;
	}

	public void setClasses(ClassInfo[] classes) {
		this.classes = classes;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

}
