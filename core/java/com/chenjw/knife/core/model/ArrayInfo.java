package com.chenjw.knife.core.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.chenjw.knife.core.result.divide.Dividable;

public class ArrayInfo implements Dividable, Serializable {

	private static final long serialVersionUID = -7795817441605184050L;
	private ObjectInfo[] elements;

	@Override
	public void divide(List<Object> fragments) {

		if (elements != null) {
			fragments.add(elements.length);
			for (ObjectInfo o : elements) {
				fragments.add(o);
			}
		} else {
			fragments.add(-1);
		}
	}

	@Override
	public void combine(Object[] fragments) {
		int num = (Integer) fragments[0];
		if (num == -1) {
			return;
		} else {

		}
		this.elements = Arrays.copyOfRange(fragments, 1, 1 + num,
				ObjectInfo[].class);
	}

	public ObjectInfo[] getElements() {
		return elements;
	}

	public void setElements(ObjectInfo[] elements) {
		this.elements = elements;
	}

}
