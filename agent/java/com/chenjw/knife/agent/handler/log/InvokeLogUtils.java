package com.chenjw.knife.agent.handler.log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Expression;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.bytecode.javassist.InvokeExpression;
import com.chenjw.bytecode.javassist.method.EnhanceMethodGenerator;
import com.chenjw.bytecode.javassist.method.EnhanceMethodGenerator.TypeEnum;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.NativeHelper;

public class InvokeLogUtils {
	private static Method INVOKE_LOG_START_METHOD = null;
	private static Method INVOKE_LOG_RETURN_END_METHOD = null;
	private static Method INVOKE_LOG_EXCEPTION_END_METHOD = null;
	// force trace class list
	private static final String[] CLASS_WHITE_LIST = new String[] { "java.lang.reflect.InvocationHandler" };
	static {
		try {
			INVOKE_LOG_START_METHOD = InvokeLog.class.getDeclaredMethod(
					"start", new Class[] { int.class, Object.class,
							String.class, String.class, Object[].class });
			INVOKE_LOG_RETURN_END_METHOD = InvokeLog.class.getDeclaredMethod(
					"returnEnd", new Class[] { int.class, Object.class,
							String.class, String.class, Object[].class,
							Object.class });
			INVOKE_LOG_EXCEPTION_END_METHOD = InvokeLog.class
					.getDeclaredMethod("exceptionEnd", new Class[] { int.class,
							Object.class, String.class, String.class,
							Object[].class, Throwable.class });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void buildMethod(int dep, ClassGenerator classGenerator,
			CtMethod ctMethod) throws Exception {
		EnhanceMethodGenerator methodGenerator = EnhanceMethodGenerator
				.newInstance(classGenerator, ctMethod);
		methodGenerator.addExpression(TypeEnum.BEFORE,
				new InvokeExpression(INVOKE_LOG_START_METHOD, null,
						new Expression[] {
								new Expression(String.valueOf(dep), int.class),
								new Expression("$0", Object.class),
								new Expression("\""
										+ classGenerator.getCtClass().getName()
										+ "\"", String.class),
								new Expression(
										"\"" + ctMethod.getName() + "\"",
										String.class),
								new Expression("$args", Object[].class) }));

		methodGenerator.addExpression(
				TypeEnum.AFTER,
				new InvokeExpression(INVOKE_LOG_RETURN_END_METHOD, null,
						new Expression[] {
								new Expression(String.valueOf(dep), int.class),
								new Expression("$0", Object.class),
								new Expression("\""
										+ classGenerator.getCtClass().getName()
										+ "\"", String.class),
								new Expression(
										"\"" + ctMethod.getName() + "\"",
										String.class),
								new Expression("$args", Object[].class),
								new Expression("$_", Helper.findClass(ctMethod
										.getReturnType())) }));
		methodGenerator.addExpression(TypeEnum.CATCH,

				new InvokeExpression(INVOKE_LOG_EXCEPTION_END_METHOD, null,
						new Expression[] {
								new Expression(String.valueOf(dep), int.class),
								new Expression("$0", Object.class),
								new Expression("\""
										+ classGenerator.getCtClass().getName()
										+ "\"", String.class),
								new Expression(
										"\"" + ctMethod.getName() + "\"",
										String.class),
								new Expression("$args", Object[].class),
								new Expression("$e", Throwable.class) }));
		classGenerator.addMethod(methodGenerator);
	}

	private static void buildClass(int dep, ClassGenerator classGenerator)
			throws Exception {
		// System.out.println("start build "
		// + classGenerator.getCtClass().getName());
		for (CtMethod ctMethod : classGenerator.getCtClass().getMethods()) {
			if (isNeedLog(ctMethod)) {
				buildMethod(dep, classGenerator, ctMethod);
			}
		}
		// System.out
		// .println("end build " + classGenerator.getCtClass().getName());
	}

	private static void buildFieldAccess(final int dep,
			ClassGenerator classGenerator) throws Exception {
		classGenerator.getCtClass().instrument(new ExprEditor() {

			@Override
			public void edit(FieldAccess fieldaccess)
					throws CannotCompileException {

				if (!isNeedLog(fieldaccess)) {
					return;
				}
				try {
					fieldaccess.replace("$_=$proceed($$);"
							+ InvokeLog.class.getName() + ".proxy(" + dep
							+ ",($w)$_);");
				} catch (CannotCompileException e) {
					Agent.println(fieldaccess.getFieldName() + " proxy error");

					throw e;
				}

			}

			/**
			 * decide if this field need be traced
			 * 
			 * @param fieldaccess
			 * @return
			 */
			private boolean isNeedLog(FieldAccess fieldaccess) {
				// not static field
				if (fieldaccess.isStatic()) {
					return false;
				}
				CtField ctField;
				try {
					ctField = fieldaccess.getField();
				} catch (NotFoundException e1) {
					return false;
				}
				CtClass ctClass;
				try {
					ctClass = Helper.getComponentType(ctField.getType());
				} catch (NotFoundException e) {
					return false;
				}
				// not primitive field
				if (ctClass.isPrimitive()) {
					return false;
				}
				// not system class field
				boolean isLog = true;
				if (ctClass.getName().startsWith("java.")) {
					isLog = false;
				} else if (ctClass.getName().startsWith("javax.")) {
					isLog = false;
				} else if (ctClass.getName().startsWith("sun.")) {
					isLog = false;
				}
				// pass for white list
				if (!isLog) {
					for (String className : CLASS_WHITE_LIST) {
						if (ctClass.getName().equals(className)) {
							isLog = true;
							break;
						}
					}
				}
				// if (isLog) {
				// System.out.println("field " + ctClass.getName() + "."
				// + ctField.getName());
				// }
				return isLog;

			}

		});
	}

	public static void buildTraceClass(int dep, Class<?> clazz)
			throws Exception {
		ClassGenerator classGenerator = ClassGenerator.newInstance(
				Helper.makeClassName(clazz), NativeHelper.getClassBytes(clazz));
		buildClass(dep, classGenerator);
		buildFieldAccess(dep, classGenerator);

		byte[] classBytes = classGenerator.toBytecode();
		// System.out.println("buildTraceClass " + clazz.getName() + ","
		// + classBytes);
		Agent.redefineClass(clazz, classBytes);
	}

	public static void buildMockClass(int dep, Class<?> clazz) throws Exception {
		ClassGenerator classGenerator = ClassGenerator.newInstance(
				Helper.makeClassName(clazz), NativeHelper.getClassBytes(clazz));
		buildClass(dep, classGenerator);
		byte[] classBytes = classGenerator.toBytecode();
		Agent.redefineClass(clazz, classBytes);
	}

	private static boolean isNeedLog(CtMethod ctMethod) {
		if (!Modifier.isPublic(ctMethod.getModifiers())) {
			return false;
		} else if (Modifier.isStatic(ctMethod.getModifiers())) {
			return false;
		} else if (ctMethod.getLongName().startsWith("java.")) {
			return false;
		} else if (ctMethod.getLongName().startsWith("javax.")) {
			return false;
		} else if (ctMethod.getLongName().startsWith("sun.")) {
			return false;
		} else {
			Method method = Helper.findMethod(ctMethod);
			if (method == null) {
				return false;
			} else if (Modifier.isNative(method.getModifiers())) {
				return false;
			} else {
				return true;
			}
		}
	}
}
