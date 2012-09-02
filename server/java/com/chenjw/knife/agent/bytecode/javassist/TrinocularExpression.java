/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.knife.agent.bytecode.javassist;

/**
 * 三目表达式
 * 
 * @author chenjw 2012-6-13 下午8:10:10
 */
public class TrinocularExpression extends Expression {

	public TrinocularExpression(Expression exp1, Expression exp2,
			Expression exp3, Class<?> type) {
		if (exp1.getType() != boolean.class && exp1.getType() != Boolean.class) {
			throw new RuntimeException("exp1(" + exp1.getType()
					+ ") must be boolean in TrinocularExpression ");
		}
		this.code = "(" + exp1.getCode() + ")?(" + exp2.cast(type).getCode()
				+ "):(" + exp3.cast(type).getCode() + ")";
		this.type = type;
	}
}
