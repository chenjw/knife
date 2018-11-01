package com.chenjw.knife.bytecode.javassist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;

import com.chenjw.knife.utils.ClassHelper;

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

	private final Set<Class<?>> interfaces = new HashSet<Class<?>>();

	private final List<String> fields = new ArrayList<String>();
	private final List<MethodGenerator> methods = new ArrayList<MethodGenerator>();

	public static ClassGenerator newInstance(String className,
			ClassPath... classPaths) {

		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initPool(null);
		classGenerator.initClassPaths(classPaths);
		classGenerator.initModifyClass(null, className);
		return classGenerator;
	}

	private static ClassGenerator newInstance(byte[] byteCode,
			ClassPath... classPaths) {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initPool(null);
		classGenerator.initClassPaths(classPaths);
		classGenerator.initModifyClass(null, byteCode);
		return classGenerator;
	}

	public static ClassGenerator newInstance(CtClass ctClass,
			ClassPath... classPaths) {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initPool(null);
		classGenerator.initClassPaths(classPaths);
		classGenerator.initModifyClass(null, ctClass);
		return classGenerator;
	}

	public static ClassGenerator newInstance(ClassPath... classPaths) {
		ClassGenerator classGenerator = new ClassGenerator();
		classGenerator.initPool(null);
		classGenerator.initClassPaths(classPaths);
		classGenerator.initNewClass(null);
		return classGenerator;
	}

	private void initPool(ClassPool classPool) {
		if (classPool == null) {
			classPool = new ClassPool(null);

		}
		this.classPool = classPool;
	}

	private void initModifyClass(ClassPool classPool, CtClass ctClass) {
		if (ctClass == null) {
			throw new RuntimeException("ctClass cant be null!");
		}

		this.ctClass = ctClass;
	}

	private void initModifyClass(ClassPool classPool, byte[] byteCode) {
		if (ctClass == null) {
			throw new RuntimeException("ctClass cant be null!");
		}
		this.ctClass = makeClass(byteCode);
		if (this.ctClass == null) {
			throw new RuntimeException("ctClass cant be null!");
		}
	}

	private void initModifyClass(ClassPool classPool, String className) {
		if (className == null) {
			throw new RuntimeException("className cant be null!");
		}
		this.ctClass = this.findCtClass(className);
		
	}

	private CtClass makeClass(byte[] classBytes) {
		InputStream is = new ByteArrayInputStream(classBytes);
		try {
			try {
				return this.classPool.makeClass(is);
			} catch (Exception e) {
				throw new RuntimeException("make from classBytes fail!", e);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				  e.printStackTrace();
				}
			}
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

	public void initClassPaths(ClassPath[] classPaths) {
		if (classPaths == null) {
			return;
		}
		for (ClassPath classPath : classPaths) {
			classPool.appendClassPath(classPath);
		}
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
		  e.printStackTrace();
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
	 * 添加静态常量，默认是private static final的
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
	public Field addField(Class<?> type, String name, Expression expr,
			boolean isStatic) {
		if (name == null) {
			name = generateClassVariableName();
		}
		this.fields.add(makeFieldStr(name, "private"
				+ (isStatic ? " static" : ""), type, expr));
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
		sb.append(ClassHelper.makeClassName(type));
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
		  e.printStackTrace();
			return null;
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
		// ctClass.writeFile("/tmp/knife/");

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

}
