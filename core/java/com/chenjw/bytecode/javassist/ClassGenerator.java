/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.bytecode.javassist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * 只实现了基本功能，定制父类，构造函数等功能未实现。
 * 
 * @author chenjw 2012-6-13 下午12:53:36
 */
public final class ClassGenerator {

	private static final AtomicLong CLASS_NAME_COUNTER = new AtomicLong(0);

	private final AtomicLong CLASS_VARIABLE_COUNTER = new AtomicLong(0);
	private ClassPool classPool;
	private CtClass ctClass;

	private Set<Class<?>> interfaces = new HashSet<Class<?>>();

	private List<String> fields = new ArrayList<String>();
	private List<MethodGenerator> methods = new ArrayList<MethodGenerator>();

	public static ClassGenerator newInstance(String className) {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initModifyClass(null, className);
		return classGenerator;
	}

	public static ClassGenerator newInstance(CtClass ctClass) {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initModifyClass(null, ctClass);
		return classGenerator;
	}

	public static ClassGenerator newInstance(String className, byte[] classBytes) {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initModifyClass(null, className, classBytes);
		return classGenerator;
	}

	public static ClassGenerator newInstance() {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initNewClass(null);
		return classGenerator;
	}

	private void initPool(ClassPool classPool) {
		if (classPool == null) {
			classPool = new ClassPool(null);
			classPool.appendSystemPath();
		}
		this.classPool = classPool;
	}

	private void initModifyClass(ClassPool classPool, CtClass ctClass) {
		if (ctClass == null) {
			throw new RuntimeException("ctClass cant be null!");
		}
		initPool(classPool);
		this.ctClass = ctClass;
	}

	private void initModifyClass(ClassPool classPool, String className) {
		if (className == null) {
			throw new RuntimeException("className cant be null!");
		}
		initPool(classPool);
		this.ctClass = this.findCtClass(className);
		if (this.ctClass == null) {
			throw new RuntimeException("ctClass cant be null!");
		}
	}

	private void initModifyClass(ClassPool classPool, String className,
			byte[] classBytes) {
		if (className == null) {
			throw new RuntimeException("className cant be null!");
		}
		if (classBytes == null) {
			throw new RuntimeException("classBytes cant be null!");
		}
		initPool(classPool);
		this.ctClass = this.findCtClass(className);
		if (this.ctClass == null) {
			appandClass(className, classBytes);
			this.ctClass = this.findCtClass(className);
		}
		if (this.ctClass == null) {
			throw new RuntimeException("ctClass cant be null!");
		}
	}

	private void initNewClass(ClassPool classPool) {
		initPool(classPool);
		String className = generateClassName();
		this.ctClass = this.createNewCtClass(className);
		if (this.ctClass == null) {
			throw new RuntimeException("ctClass not found!");
		}
	}

	private ClassGenerator() {
	}

	private void appandClass(String className, byte[] classBytes) {
		classPool
				.appendClassPath(new ByteArrayClassPath(className, classBytes));
	}

	private CtClass createNewCtClass(String className) {
		CtClass newCtClass = classPool.makeClass(className);
		// 添加默认构造函数
		try {
			newCtClass.addConstructor(CtNewConstructor
					.defaultConstructor(newCtClass));
			// 可见性修改为public
			newCtClass.setModifiers(newCtClass.getModifiers()
					& ~Modifier.ABSTRACT);
			return newCtClass;
		} catch (CannotCompileException e) {
			return null;
		}
	}

	/**
	 * 添加接口
	 * 
	 * @param clazz
	 */
	public void addInterface(Class<?> clazz) {
		interfaces.add(clazz);
	}

	/**
	 * 生成类变/常量名称
	 * 
	 * @return
	 */
	private String generateClassVariableName() {
		return "tmp_cv_" + CLASS_VARIABLE_COUNTER.getAndIncrement();
	}

	/**
	 * 生成类名称
	 * 
	 * @return
	 */
	private static String generateClassName() {
		return ClassGenerator.class.getName() + "$$Javassist$$"
				+ CLASS_NAME_COUNTER.getAndIncrement();
	}

	/**
	 * 添加静态常量，默认是private statif final的
	 * 
	 * @param type
	 * @param expr
	 * @return
	 */
	public Field addStaticFinalField(Class<?> type, Expression expr) {
		String name = generateClassVariableName();
		this.fields.add(makeFieldStr(name, "private static final", type, expr));
		return new Field(name, type, true);
	}

	/**
	 * 添加方法，每个方法有相应的构造器
	 * 
	 * @param methodGenerator
	 */
	public void addMethod(MethodGenerator methodGenerator) {
		this.methods.add(methodGenerator);
	}

	/**
	 * 添加类属性，默认是private的
	 * 
	 * @param type
	 * @param expr
	 * @return
	 */
	public Field addField(Class<?> type, Expression expr) {
		String name = generateClassVariableName();
		this.fields.add(makeFieldStr(name, "private", type, expr));
		return new Field(name, type, false);
	}

	/**
	 * 生成属性定义的code
	 * 
	 * @param name
	 * @param desc
	 * @param type
	 * @param expr
	 * @return
	 */
	private String makeFieldStr(String name, String desc, Class<?> type,
			Expression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append(desc);
		sb.append(' ');
		sb.append(Helper.makeClassName(type));
		sb.append(' ');
		sb.append(name);
		if (expr != null) {
			sb.append('=');
			sb.append(expr.cast(type).getCode());
		}
		sb.append(';');
		return sb.toString();
	}

	public CtClass findCtClass(String className) {
		CtClass targetCtClass = null;
		try {
			targetCtClass = classPool.getCtClass(className);
		} catch (NotFoundException e) {
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
				// System.out.println(clazz.getClassLoader());
				classPool.appendClassPath(new LoaderClassPath(clazz
						.getClassLoader()));
				try {
					return classPool.getCtClass(className);
				} catch (NotFoundException e1) {
					return null;
				}
			} catch (ClassNotFoundException e1) {
				return null;
			}
		}
		return targetCtClass;

	}

	private void build() throws NotFoundException, CannotCompileException,
			IOException {

		// 添加需要实现的接口
		for (Class<?> interf : interfaces) {
			ctClass.addInterface(findCtClass(interf.getName()));
		}
		// 添加属性
		for (String field : fields) {
			ctClass.addField(CtField.make(field, ctClass));

		}
		// 添加方法
		for (MethodGenerator methodGenerator : methods) {
			methodGenerator.generate(ctClass);
		}

		// 要看生成的class文件，可以开打这个注释（需要自行反编译）
		ctClass.writeFile("/tmp/knife/");

		// ctClass.rebuildClassFile();
		// return Class.forName(ctClass.getName());

	}

	private void finish() {
		ctClass.defrost();
	}

	public CtClass getCtClass() {
		return ctClass;
	}

	/**
	 * 根据定义生成需要的class
	 * 
	 * @return
	 */
	public Class<?> toClass() {
		try {
			build();
			return ctClass.toClass();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			finish();
		}
	}

	public byte[] toBytecode() {
		try {
			build();
			return ctClass.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			finish();
		}
	}

	public static void main(String[] args) {
		Pattern pattern = Pattern
				.compile("/member/midway(_)?[aA]v(_)?[aA]lert.htm");

		Matcher matcher = pattern
				.matcher("http://exodus2.china.alibaba.com:2100/member/midway_av_alert.htm");
		System.out.println(matcher.find());

	}
}
