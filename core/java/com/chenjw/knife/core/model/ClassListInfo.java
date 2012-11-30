package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ClassListInfo implements Serializable {

	private static final long serialVersionUID = 7855978950886901067L;
	private String expretion;
	private ClassInfo[] classes;

	public ClassInfo[] getClasses() {
		return classes;
	}

	public void setClasses(ClassInfo[] classes) {
		this.classes = classes;
	}

	public String getExpretion() {
		return expretion;
	}

	public void setExpretion(String expretion) {
		this.expretion = expretion;
	}

}
