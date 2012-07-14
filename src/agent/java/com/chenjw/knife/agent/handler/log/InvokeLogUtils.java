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

public class InvokeLogUtils {
	private static Method INVOKE_LOG_METHOD = null;
	static {
		try {
			INVOKE_LOG_METHOD = InvokeLog.class.getDeclaredMethod("logInvoke",
					new Class[] { String.class, String.class, Object[].class,
							Object.class, Throwable.class });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void buildMethod(ClassGenerator classGenerator,
			CtMethod ctMethod) throws Exception {
		EnhanceMethodGenerator methodGenerator = EnhanceMethodGenerator
				.newInstance(classGenerator, ctMethod);
		methodGenerator.addExpression(
				TypeEnum.AFTER,
				new InvokeExpression(INVOKE_LOG_METHOD, null, new Expression[] {
						new Expression("\""
								+ classGenerator.getCtClass().getName() + "\"",
								String.class),
						new Expression("\"" + ctMethod.getName() + "\"",
								String.class),
						new Expression("$args", Object[].class),
						new Expression("$_", Helper.findClass(ctMethod
								.getReturnType())),
						new Expression("null", Throwable.class) }));
		methodGenerator.addExpression(
				TypeEnum.CATCH,

				new InvokeExpression(INVOKE_LOG_METHOD, null, new Expression[] {
						new Expression("\""
								+ classGenerator.getCtClass().getName() + "\"",
								String.class),
						new Expression("\"" + ctMethod.getName() + "\"",
								String.class),
						new Expression("$args", Object[].class),

						new Expression("null", Helper.findClass(ctMethod
								.getReturnType())),
						new Expression("$e", Throwable.class) }));
		classGenerator.addMethod(methodGenerator);
	}

	private static void buildClass(ClassGenerator classGenerator)
			throws Exception {
		// System.out.println("start build "
		// + classGenerator.getCtClass().getName());
		for (CtMethod ctMethod : classGenerator.getCtClass().getMethods()) {
			if (isNeedLog(ctMethod)) {
				buildMethod(classGenerator, ctMethod);
			}
		}
		// System.out
		// .println("end build " + classGenerator.getCtClass().getName());
	}

	private static void buildFieldAccess(ClassGenerator classGenerator)
			throws Exception {
		classGenerator.getCtClass().instrument(new ExprEditor() {

			@Override
			public void edit(FieldAccess fieldaccess)
					throws CannotCompileException {
				if (fieldaccess.isStatic()) {
					return;
				}
				try {
					if (!isNeedLog(fieldaccess.getField())) {
						return;
					}
				} catch (NotFoundException e) {
					return;
				}
				try {
					fieldaccess.replace("$_=$proceed($$);"
							+ InvokeLog.class.getName() + ".proxy(($w)$_);");
				} catch (CannotCompileException e) {
					Agent.print(fieldaccess.getFieldName() + " proxy error");

					throw e;
				}

			}

			private boolean isNeedLog(CtField ctField) {
				CtClass ctClass;
				try {
					ctClass = Helper.getComponentType(ctField.getType());
				} catch (NotFoundException e) {
					return false;
				}
				if (ctClass.getName().startsWith("java.")) {
					return false;
				} else if (ctClass.getName().startsWith("javax.")) {
					return false;
				} else if (ctClass.getName().startsWith("sun.")) {
					return false;
				} else {
					return true;
				}
			}

		});
	}

	public static void buildTraceClass(Class<?> clazz) throws Exception {
		ClassGenerator classGenerator = ClassGenerator.newInstance(Helper
				.makeClassName(clazz));
		buildClass(classGenerator);
		buildFieldAccess(classGenerator);
		Agent.redefineClass(Helper.findClass(classGenerator.getCtClass()),
				classGenerator.toBytecode());
	}

	public static void buildMockClass(Class<?> clazz) throws Exception {
		ClassGenerator classGenerator = ClassGenerator.newInstance(Helper
				.makeClassName(clazz));
		buildClass(classGenerator);
		Agent.redefineClass(Helper.findClass(classGenerator.getCtClass()),
				classGenerator.toBytecode());
	}

	private static boolean isNeedLog(CtMethod ctMethod) {
		if (!Modifier.isPublic(ctMethod.getModifiers())) {
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
