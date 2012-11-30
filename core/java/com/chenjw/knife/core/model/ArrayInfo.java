package com.chenjw.knife.core.model;

import java.io.Serializable;

public class ArrayInfo implements Serializable {

	private static final long serialVersionUID = -7795817441605184050L;
	private ObjectInfo[] elements;

	public ObjectInfo[] getElements() {
		return elements;
	}

	public void setElements(ObjectInfo[] elements) {
		this.elements = elements;
	}

}
