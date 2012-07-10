/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.bytecode.javassist;

import java.lang.reflect.Method;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;

/**
 * 工具类
 * 
 * @author chenjw 2012-6-14 上午12:19:48
 */
public class Helper {
	/**
	 * 从基础类型获得封装类型
	 * 
	 * @param clazz
	 * @return 封装类型
	 */
	public static Class<?> getBoxClazz(Class<?> clazz) {
		if (!clazz.isPrimitive()) {
			return clazz;
		}
		if (clazz == int.class) {
			return Integer.class;
		} else if (clazz == long.class) {
			return Long.class;
		} else if (clazz == float.class) {
			return Float.class;
		} else if (clazz == double.class) {
			return Double.class;
		} else if (clazz == short.class) {
			return Short.class;
		} else if (clazz == boolean.class) {
			return Boolean.class;
		} else if (clazz == byte.class) {
			return Byte.class;
		} else if (clazz == char.class) {
			return Character.class;
		} else {
			return clazz;
		}
	}

	/**
	 * 根据class获得表示类型的字符串
	 * 
	 * @param c
	 * @return
	 */
	public static String makeClassName(Class<?> c) {
		if (c.isArray()) {
			StringBuilder sb = new StringBuilder();
			do {
				sb.append("[]");
				c = c.getComponentType();
			} while (c.isArray());

			return c.getName() + sb.toString();
		}
		return c.getName();
	}

	private static String getBasicJvmClassName(String name) {
		if ("boolean".equals(name)) {
			return "Z";
		} else if ("char".equals(name)) {
			return "C";
		} else if ("byte".equals(name)) {
			return "B";
		} else if ("short".equals(name)) {
			return "S";
		} else if ("int".equals(name)) {
			return "I";
		} else if ("long".equals(name)) {
			return "J";
		} else if ("float".equals(name)) {
			return "F";
		} else if ("double".equals(name)) {
			return "D";
		} else if ("void".equals(name)) {
			return "V";
		} else {
			return null;
		}
	}

	private static Class<?> findPrimitiveClass(String name) {
		if ("int".equals(name)) {
			return int.class;
		} else if ("long".equals(name)) {
			return long.class;
		} else if ("float".equals(name)) {
			return float.class;
		} else if ("short".equals(name)) {
			return short.class;
		} else if ("double".equals(name)) {
			return double.class;
		} else if ("char".equals(name)) {
			return char.class;
		} else if ("boolean".equals(name)) {
			return boolean.class;
		} else if ("byte".equals(name)) {
			return byte.class;
		} else if ("void".equals(name)) {
			return void.class;
		} else {
			return null;
		}
	}

	public static CtClass getComponentType(CtClass ctClass) {
		while (ctClass.isArray()) {
			try {
				ctClass = ctClass.getComponentType();
			} catch (NotFoundException e) {
				return ctClass;
			}
		}
		return ctClass;
	}

	public static Class<?> findClass(String className) {
		Class<?> clazz = findPrimitiveClass(className);
		if (clazz == null) {
			// 数组类型的
			boolean isFirst = true;
			String as = "";
			// System.err.println(className);
			while (className.endsWith("[]")) {
				as += "[";
				className = StringUtils.substringBeforeLast(className, "[]");
				isFirst = false;
				// System.err.println(className);
			}
			if (!isFirst) {
				String basicJvmClassName = getBasicJvmClassName(className);
				if (basicJvmClassName == null) {
					className = as + "L" + className + ";";
				} else {
					className = as + basicJvmClassName;
				}
			}
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}

	public static Class<?> findClass(CtClass ctClass) {
		return findClass(ctClass.getName());
	}

	public static Method findMethod(CtMethod ctMethod) {
		try {
			CtClass ctClass = ctMethod.getDeclaringClass();
			Class<?> clazz = Helper.findClass(ctClass);
			String methodName = ctMethod.getName();
			Class<?>[] pClass = new Class<?>[ctMethod.getParameterTypes().length];
			CtClass[] pCtClass = ctMethod.getParameterTypes();
			for (int i = 0; i < pCtClass.length; i++) {
				pClass[i] = Helper.findClass(pCtClass[i]);
			}
			return clazz.getMethod(methodName, pClass);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] getBytecode(Class<?> clazz) {
		ClassPool classPool = ClassPool.getDefault();
		CtClass targetCtClass = null;
		try {
			targetCtClass = classPool.getCtClass(clazz.getName());
		} catch (NotFoundException e) {
			try {
				classPool.appendClassPath(new ClassClassPath(clazz));
				targetCtClass = classPool.getCtClass(clazz.getName());
			} catch (Exception e1) {
				targetCtClass = null;
			}
		}
		if (targetCtClass == null) {
			return null;
		} else {
			try {
				byte[] bytecode = targetCtClass.toBytecode();
				targetCtClass.defrost();
				return bytecode;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 生成表示某个字符串常量的表达式
	 * 
	 * @param str
	 * @return
	 */
	public static Expression createStringExpression(String str) {
		return new Expression("\"" + str + "\"", String.class);
	}

	/**
	 * 根据class调用默认构造函数创建实例
	 * 
	 * @param clazz
	 * @return
	 */
	public static Object newInstance(Class<?> clazz) {
		try {
			return clazz.getConstructor(new Class[0])
					.newInstance(new Object[0]);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据原始类型和目标类型判断是否需要解包
	 * 
	 * @param destClazz
	 * @return
	 */
	public static boolean isNeedUnbox(Class<?> originClazz, Class<?> destClazz) {
		if (destClazz.isPrimitive() && !originClazz.isPrimitive()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据原始类型和目标类型判断是否需要装包
	 * 
	 * @param originClazz
	 * @param destClazz
	 * @return
	 */
	public static boolean isNeedBox(Class<?> originClazz, Class<?> destClazz) {
		if (originClazz.isPrimitive() && !destClazz.isPrimitive()) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws NotFoundException,
			ClassNotFoundException {
		CtMethod ctMethod = ClassPool.getDefault().getMethod(
				"com.chenjw.attach.agent.handler.log.InvokeLog", "logInvoke1");
		// System.out.println(Helper.findClass(ctMethod.getReturnType()));
		System.out.println(Class.forName("[[I"));

		System.out.println(int[][].class.getName());
		System.out.println(Integer[][].class.getName());
		System.out.println(Helper.findClass(int[][].class.getName()));
		System.out.println(ClassPool.getDefault().get("void").getName());
		System.out.println(ClassPool.getDefault().get("java.lang.Void")
				.getName());
		System.out.println(Void.class.isAssignableFrom(Object.class));
		System.out.println(new byte[0] instanceof Object);
	}
}
