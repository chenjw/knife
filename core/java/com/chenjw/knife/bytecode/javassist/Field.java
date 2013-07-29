package com.chenjw.knife.bytecode.javassist;

/**
 * 类属性或者方法属性
 * 
 * @author chenjw 2012-6-14 上午12:19:20
 */
public class Field extends Expression {

	// 该属性是否为常量
	protected boolean isFinal;

	public Field(String name, Class<?> type, boolean isFinal) {

		this.code = name;
		this.type = type;
		this.isFinal = isFinal;
	}

	public boolean isFinal() {
		return isFinal;
	}

}
