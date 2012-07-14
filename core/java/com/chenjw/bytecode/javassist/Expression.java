/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.bytecode.javassist;

/**
 * 普通表达式
 * 
 * @author chenjw 2012-6-14 上午12:18:28
 */
public class Expression {

	// 表达式字符串
	protected String code;
	// 该表达式期望返回的结果类型
	protected Class<?> type;

	protected Expression() {
	}

	public Expression(String code, Class<?> type) {
		this.code = code;
		this.type = type;
	}

	public Expression cast(Class<?> destType) {
		if (isSubClassOf(this.getType(), destType)) {
			return this;
		}
		return new Expression("(" + Helper.makeClassName(destType) + ")("
				+ this.getCode() + ")", destType);
	}

	private static boolean isSubClassOf(Class<?> subClass, Class<?> clazz) {
		try {
			return clazz.isAssignableFrom(subClass);
		} catch (NullPointerException e) {
			// System.out.println(subClass);
			// System.out.println(clazz);
			throw e;
		}

	}

	/**
	 * 加包
	 * 
	 * @param destClazz
	 * @return
	 */
	public Expression box(Class<?> destClazz) {
		Class<?> originClazz = getType();
		if (!Helper.isNeedBox(originClazz, destClazz)) {
			return this;
		}
		if (originClazz == boolean.class) {
			return new Expression("($w)(" + getCode() + ")", Boolean.class);
		} else if (originClazz == byte.class) {
			return new Expression("($w)(" + getCode() + ")", Byte.class);
		} else if (originClazz == char.class) {
			return new Expression("($w)(" + getCode() + ")", Character.class);
		} else if (originClazz == double.class) {
			return new Expression("($w)(" + getCode() + ")", Double.class);
		} else if (originClazz == float.class) {
			return new Expression("($w)(" + getCode() + ")", Float.class);
		} else if (originClazz == int.class) {
			return new Expression("($w)(" + getCode() + ")", Integer.class);
		} else if (originClazz == long.class) {
			return new Expression("($w)(" + getCode() + ")", Long.class);
		} else if (originClazz == short.class) {
			return new Expression("($w)(" + getCode() + ")", Short.class);
		}
		return this;

	}

	/**
	 * 解包，如果原始值为null，返回相应的默认值
	 * 
	 * @param destClazz
	 * @return
	 */
	public Expression unboxOrZero(Class<?> destClazz) {
		Class<?> originClazz = getType();
		if (!Helper.isNeedUnbox(originClazz, destClazz)) {
			return this;
		}
		if (destClazz == boolean.class) {
			String code = "(" + cast(Boolean.class).getCode() + ")";
			return new Expression(code + "==null?false:" + code
					+ ".booleanValue()", boolean.class);
		} else if (destClazz == byte.class) {
			String code = "(" + cast(Byte.class).getCode() + ")";
			return new Expression(code + "==null?0:" + code + ".byteValue()",
					byte.class);
		} else if (destClazz == char.class) {
			String code = "(" + cast(Character.class).getCode() + ")";
			return new Expression(code + "==null?'':" + code + ".charValue()",
					char.class);
		} else if (destClazz == double.class) {
			String code = "(" + cast(Double.class).getCode() + ")";
			return new Expression(code + "==null?0.0D:" + code
					+ ".doubleValue()", double.class);
		} else if (destClazz == float.class) {
			String code = "(" + cast(Float.class).getCode() + ")";
			return new Expression(code + "==null?0.0F:" + code
					+ ".floatValue()", float.class);
		} else if (destClazz == int.class) {
			String code = "(" + cast(Integer.class).getCode() + ")";
			return new Expression(code + "==null?0:" + code + ".intValue()",
					int.class);
		} else if (destClazz == long.class) {
			String code = "(" + cast(Long.class).getCode() + ")";
			return new Expression(code + "==null?0L:" + code + ".longValue()",
					long.class);
		} else if (destClazz == short.class) {
			String code = "(" + cast(Short.class).getCode() + ")";
			return new Expression(code + "==null?0:" + code + ".shortValue()",
					short.class);
		}
		return this;

	}

	public String getCode() {
		return code;
	}

	public Class<?> getType() {
		return type;
	}

}
