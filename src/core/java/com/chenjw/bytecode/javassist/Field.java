/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.bytecode.javassist;

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
