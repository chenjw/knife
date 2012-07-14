/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.bytecode.javassist.method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Expression;
import com.chenjw.bytecode.javassist.Field;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.bytecode.javassist.MethodGenerator;

/**
 * 方法构建器
 * 
 * @author chenjw 2012-6-13 上午11:46:07
 */
public final class EnhanceMethodGenerator implements MethodGenerator {

	private final AtomicLong METHOD_COUNTER = new AtomicLong(0);
	private final AtomicLong METHOD_VARIABLE_COUNTER = new AtomicLong(0);
	private ClassGenerator classGenerator;
	private CtMethod oldCtMethod;
	private List<String> beforeMethodLines = new ArrayList<String>();
	private List<String> afterMethodLines = new ArrayList<String>();
	private List<String> catchMethodLines = new ArrayList<String>();
	private List<String> finallyMethodLines = new ArrayList<String>();

	private String lineListToCode(List<String> lines) {
		if (lines == null || lines.size() == 0) {
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			for (String line : lines) {
				sb.append(line);
			}
			return sb.toString();
		}

	}

	public void generate(CtClass ctClass) throws CannotCompileException,
			NotFoundException {
		String beforeCode = lineListToCode(beforeMethodLines);
		if (beforeCode != null) {
			oldCtMethod.insertBefore(beforeCode);
		}
		String afterCode = lineListToCode(afterMethodLines);
		if (afterCode != null) {
			// System.err.println(afterCode);
			try {
				oldCtMethod.insertAfter(afterCode, false);
			} catch (CannotCompileException e) {
				throw e;
			}

		}
		String catchCode = lineListToCode(catchMethodLines);
		if (catchCode != null) {
			oldCtMethod.addCatch(catchCode + "throw $e;",
					classGenerator.findCtClass("java.lang.Throwable"));
		}
		String finallyCode = lineListToCode(finallyMethodLines);
		if (finallyCode != null) {
			oldCtMethod.insertAfter(finallyCode, true);
		}
	}

	public static EnhanceMethodGenerator newInstance(
			ClassGenerator classGenerator, String methodDesc) {
		CtMethod ctMethod = null;
		try {
			ctMethod = classGenerator
					.getCtClass()
					.getClassPool()
					.getMethod(classGenerator.getCtClass().getName(),
							methodDesc);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return new EnhanceMethodGenerator(classGenerator, ctMethod);
	}

	public static EnhanceMethodGenerator newInstance(
			ClassGenerator classGenerator, CtMethod oldCtMethod) {
		return new EnhanceMethodGenerator(classGenerator, oldCtMethod);
	}

	private EnhanceMethodGenerator(ClassGenerator classGenerator,
			CtMethod oldCtMethod) {
		this.classGenerator = classGenerator;

		this.oldCtMethod = oldCtMethod;
	}

	/**
	 * 方法变量名称
	 * 
	 * @return
	 */
	private String generateMethodVariableName() {
		return "$emg_mv_" + METHOD_VARIABLE_COUNTER.getAndIncrement();
	}

	/**
	 * 增加方法本地变量的定义
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public Field addLocalVariableDefine(TypeEnum linetype, Class<?> type,
			Expression expr) {
		String name = generateMethodVariableName();
		StringBuilder sb = new StringBuilder();
		sb.append(Helper.makeClassName(type)).append(' ');
		sb.append(name);
		if (expr != null) {
			sb.append('=');
			sb.append(expr.cast(type).getCode());
		}
		sb.append(';');
		addLine(linetype, sb.toString());
		return new Field(name, type, false);
	}

	private void addLine(TypeEnum linetype, String line) {
		switch (linetype) {
		case BEFORE:
			beforeMethodLines.add(line);
			break;
		case AFTER:
			afterMethodLines.add(line);
			break;
		case CATCH:
			catchMethodLines.add(line);
			break;
		case FINALLY:
			finallyMethodLines.add(line);
			break;
		}
	}

	/**
	 * 增加变量的赋值
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public void addVariableAssign(TypeEnum linetype, Field field,
			Expression expr) {
		if (field.isFinal()) {
			throw new IllegalStateException("final field(" + field.getCode()
					+ ") cant be assign");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(field.getCode());
		sb.append('=');
		sb.append(expr.cast(field.getType()).getCode());
		sb.append(';');
		addLine(linetype, sb.toString());
	}

	/**
	 * 表达式语句
	 * 
	 * @param expr
	 */
	public void addExpression(TypeEnum linetype, Expression expr) {
		addLine(linetype, expr.getCode() + ';');
	}

	public enum TypeEnum {
		BEFORE, AFTER, CATCH, FINALLY
	}

}
