package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ClassMethodInfo implements Serializable {

	private static final long serialVersionUID = 2300902311470176430L;
	private MethodInfo[] methods;

	public MethodInfo[] getMethods() {
		return methods;
	}

	public void setMethods(MethodInfo[] methods) {
		this.methods = methods;
	}

}
