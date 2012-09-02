/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.knife.agent.bytecode.javassist.method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import com.chenjw.knife.agent.bytecode.javassist.Expression;
import com.chenjw.knife.agent.bytecode.javassist.Field;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.bytecode.javassist.MethodGenerator;

/**
 * 方法构建器
 * 
 * @author chenjw 2012-6-13 上午11:46:07
 */
public final class NewMethodGenerator implements MethodGenerator {
	private final AtomicLong METHOD_COUNTER = new AtomicLong(0);
	private final AtomicLong METHOD_VARIABLE_COUNTER = new AtomicLong(0);

	private String signature;
	private List<String> methodLines = new ArrayList<String>();

	private NewMethodGenerator(String modifier, String name,
			Class<?>[] paramTypes, Class<?> returnType,
			Class<? extends Throwable>[] exceptionTypes) {
		StringBuffer sb = new StringBuffer();
		sb.append(modifier);
		sb.append(" ");
		sb.append(Helper.makeClassName(returnType));
		sb.append(" ");
		sb.append(name);
		sb.append("(");
		if (paramTypes != null && paramTypes.length > 0) {
			int i = 0;
			for (Class<?> paramType : paramTypes) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(Helper.makeClassName(paramType));
			}
		}
		sb.append(")");
		if (exceptionTypes != null && exceptionTypes.length > 0) {
			sb.append("throws ");
			int i = 0;
			for (Class<? extends Throwable> exceptionType : exceptionTypes) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(Helper.makeClassName(exceptionType));
			}
		}
		this.signature = sb.toString();
	}

	private String getMethodCode() {
		StringBuffer sb = new StringBuffer();
		sb.append(signature);
		sb.append('{');
		for (String line : methodLines) {
			sb.append(line);
		}
		sb.append('}');
		return sb.toString();
	}

	public void generate(CtClass ctClass) throws CannotCompileException {
		CtMethod ctMethod = CtNewMethod.make(getMethodCode(), ctClass);
		// 方法可见性默认修改为public
		ctMethod.setModifiers(ctMethod.getModifiers() & ~Modifier.ABSTRACT);
		ctClass.addMethod(ctMethod);
	}

	public static NewMethodGenerator newInstance(String modifier, String name,
			Class<?>[] paramTypes, Class<?> returnType,
			Class<? extends Throwable>[] exceptionTypes) {
		return new NewMethodGenerator(modifier, name, paramTypes, returnType,
				exceptionTypes);
	}

	public List<String> getMethodLines() {
		return methodLines;
	}

	public String getSignature() {
		return signature;
	}

	/**
	 * 方法变量名称
	 * 
	 * @return
	 */
	private String generateMethodName() {
		return "$nmg_m_" + METHOD_COUNTER.getAndIncrement();
	}

	/**
	 * 方法变量名称
	 * 
	 * @return
	 */
	private String generateMethodVariableName() {
		return "$nmg_mv_" + METHOD_VARIABLE_COUNTER.getAndIncrement();
	}

	/**
	 * 增加方法本地变量的定义
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public Field addLocalVariableDefine(Class<?> type, Expression expr) {
		String name = generateMethodVariableName();
		StringBuilder sb = new StringBuilder();
		sb.append(Helper.makeClassName(type)).append(' ');
		sb.append(name);
		if (expr != null) {
			sb.append('=');
			sb.append(expr.cast(type).getCode());
		}
		sb.append(';');
		methodLines.add(sb.toString());
		return new Field(name, type, false);
	}

	/**
	 * 增加变量的赋值
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public void addVariableAssign(Field field, Expression expr) {
		if (field.isFinal()) {
			throw new IllegalStateException("final field(" + field.getCode()
					+ ") cant be assign");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(field.getCode());
		sb.append('=');
		sb.append(expr.cast(field.getType()).getCode());
		sb.append(';');
		methodLines.add(sb.toString());
	}

	/**
	 * 表达式语句
	 * 
	 * @param expr
	 */
	public void addExpression(Expression expr) {
		methodLines.add(expr.getCode() + ';');
	}

}
