package com.chenjw.knife.core.model.result;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.chenjw.knife.core.model.divide.Dividable;

public class ClassListInfo implements Dividable, Serializable {

	private static final long serialVersionUID = 7855978950886901067L;
	private String expression;
	private ClassInfo[] classes;

	@Override
	public void divide(List<Object> fragments) {
		fragments.add(expression);
		if (classes != null) {
			fragments.add(classes.length);
			for (ClassInfo classInfo : classes) {
				fragments.add(classInfo);
			}
		} else {
			fragments.add(0);
		}
	}

	@Override
	public void combine(Object[] fragments) {
		this.expression = (String) fragments[0];
		int num = (Integer) fragments[1];
		this.classes = Arrays.copyOfRange(fragments, 2, 2 + num,
				ClassInfo[].class);
	}

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
