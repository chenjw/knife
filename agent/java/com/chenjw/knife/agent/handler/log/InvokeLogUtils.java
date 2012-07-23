package com.chenjw.knife.agent.handler.log;

import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.NativeHelper;

public class InvokeLogUtils {
	// private static Method INVOKE_LOG_START_METHOD = null;
	// private static Method INVOKE_LOG_RETURN_END_METHOD = null;
	// private static Method INVOKE_LOG_EXCEPTION_END_METHOD = null;
	// force trace class list
	private static final String[] CLASS_WHITE_LIST = new String[] {
			"java.lang.reflect.InvocationHandler.invoke",
			"java.lang.reflect.Method.invoke" };
	static {
		// try {
		// INVOKE_LOG_START_METHOD = InvokeLog.class.getDeclaredMethod(
		// "start", new Class[] { Object.class, String.class,
		// String.class, Object[].class });
		// INVOKE_LOG_RETURN_END_METHOD = InvokeLog.class.getDeclaredMethod(
		// "returnEnd", new Class[] { Object.class, String.class,
		// String.class, Object[].class, Object.class });
		// INVOKE_LOG_EXCEPTION_END_METHOD = InvokeLog.class
		// .getDeclaredMethod("exceptionEnd", new Class[] {
		// Object.class, String.class, String.class,
		// Object[].class, Throwable.class });
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private static void buildMethod(ClassGenerator classGenerator,
			CtMethod ctMethod) throws Exception {
		// EnhanceMethodGenerator methodGenerator = EnhanceMethodGenerator
		// .newInstance(classGenerator, ctMethod);
		// methodGenerator.addExpression(TypeEnum.BEFORE,
		// new InvokeExpression(INVOKE_LOG_START_METHOD, null,
		// new Expression[] {
		// new Expression(String.valueOf(dep), int.class),
		// new Expression("$0", Object.class),
		// new Expression("\""
		// + classGenerator.getCtClass().getName()
		// + "\"", String.class),
		// new Expression(
		// "\"" + ctMethod.getName() + "\"",
		// String.class),
		// new Expression("$args", Object[].class) }));

		// methodGenerator.addExpression(
		// TypeEnum.AFTER,
		// new InvokeExpression(INVOKE_LOG_RETURN_END_METHOD, null,
		// new Expression[] {
		// new Expression(String.valueOf(dep), int.class),
		// new Expression("$0", Object.class),
		// new Expression("\""
		// + classGenerator.getCtClass().getName()
		// + "\"", String.class),
		// new Expression(
		// "\"" + ctMethod.getName() + "\"",
		// String.class),
		// new Expression("$args", Object[].class),
		// new Expression("$_", Helper.findClass(ctMethod
		// .getReturnType())) }));

		// methodGenerator.addExpression(TypeEnum.CATCH,
		//
		// new InvokeExpression(INVOKE_LOG_EXCEPTION_END_METHOD, null,
		// new Expression[] {
		// new Expression(String.valueOf(dep), int.class),
		// new Expression("$0", Object.class),
		// new Expression("\""
		// + classGenerator.getCtClass().getName()
		// + "\"", String.class),
		// new Expression(
		// "\"" + ctMethod.getName() + "\"",
		// String.class),
		// new Expression("$args", Object[].class),
		// new Expression("$e", Throwable.class) }));
		// classGenerator.addMethod(methodGenerator);
	}

	private static void buildClass(ClassGenerator classGenerator)
			throws Exception {
		for (CtMethod ctMethod : classGenerator.getCtClass().getMethods()) {
			if (isNeedLog(ctMethod)) {
				buildMethod(classGenerator, ctMethod);
			}
		}
		// System.out
		// .println("end build " + classGenerator.getCtClass().getName());
	}

	private static void buildFieldAccess(final ClassGenerator classGenerator)
			throws Exception {
		classGenerator.getCtClass().instrument(new ExprEditor() {
			public void edit(MethodCall methodcall)
					throws CannotCompileException {
				// System.out.println(methodcall.getClassName() + "."
				// + methodcall.getMethodName());
				if (!isNeedLog(methodcall)) {
					return;
				}
				// if (!StringUtils.equals(methodcall.getClassName(),
				// classGenerator.getCtClass().getName())) {
				// InvokeLog.proxy(Helper.findClass(methodcall.getClassName()));
				// // Agent.println(dep + 1 + methodcall.getClassName());
				// }

				String proxyCode = InvokeLog.class.getName()
						+ ".traceObject($0);";

				if ("java.lang.reflect.Method".equals(methodcall.getClassName())
						&& "invoke".equals(methodcall.getMethodName())) {

					proxyCode = InvokeLog.class.getName() + ".traceObject($1);";
				}
				if (isStatic(methodcall)) {
					proxyCode = InvokeLog.class.getName()
							+ ".traceClass($class);";
				}
				String startCode = InvokeLog.class.getName() + ".start($0,\""
						+ methodcall.getClassName() + "\",\""
						+ methodcall.getMethodName() + "\",$args);";
				String returnEndCode = InvokeLog.class.getName()
						+ ".returnEnd( $0,\"" + methodcall.getClassName()
						+ "\",\"" + methodcall.getMethodName()
						+ "\",$args,($w)$_);";

				String exceptionEndCode = InvokeLog.class.getName()
						+ ".exceptionEnd( $0,\"" + methodcall.getClassName()
						+ "\",\"" + methodcall.getMethodName()
						+ "\",$args,$e);";
				String code = "try{";
				code += startCode;
				code += proxyCode;
				code += "$_ = $proceed($$);";
				code += returnEndCode;
				code += "}";
				code += "catch(java.lang.Throwable $e){";
				code += exceptionEndCode;
				code += "throw $e;";
				code += "}";
				// System.out.println(methodcall.getClassName() + "."
				// + methodcall.getMethodName());
				// System.out.println(code);

				methodcall.replace(code);

			}

			private boolean isStatic(MethodCall methodcall) {
				try {
					return Modifier.isStatic(methodcall.getMethod()
							.getModifiers());
				} catch (NotFoundException e) {
					return false;
				}
			}

			// new Expression[] {
			// new Expression(String.valueOf(dep), int.class),
			// new Expression("$0", Object.class),
			// new Expression("\""
			// + classGenerator.getCtClass().getName()
			// + "\"", String.class),
			// new Expression(
			// "\"" + ctMethod.getName() + "\"",
			// String.class),
			// new Expression("$args", Object[].class),
			// new Expression("$_", Helper.findClass(ctMethod
			// .getReturnType())) }));
			@Override
			public void edit(FieldAccess fieldaccess)
					throws CannotCompileException {

				// if (!isNeedLog(fieldaccess)) {
				// return;
				// }
				// try {
				// fieldaccess.replace("$_=$proceed($$);"
				// + InvokeLog.class.getName() + ".proxy(($w)$_);");
				// } catch (CannotCompileException e) {
				// Agent.println(fieldaccess.getFieldName() + " proxy error");
				//
				// throw e;
				// }

			}

			//
			// private boolean isNeedLog(CtClass ctClass) {
			// ctClass = Helper.getComponentType(ctClass);
			// // not primitive field
			// if (ctClass.isPrimitive()) {
			// return false;
			// }
			// return isNeedLog(ctClass.getName());
			// }

			private boolean isNeedLog(String className, String methodName) {
				// not system class field
				boolean isLog = true;
				String name = className + "." + methodName;
				if (name.startsWith("java.")) {
					isLog = false;
				} else if (name.startsWith("javax.")) {
					isLog = false;
				} else if (name.startsWith("sun.")) {
					isLog = false;
				}
				// pass for white list
				if (!isLog) {
					for (String cn : CLASS_WHITE_LIST) {
						if (name.equals(cn)) {
							isLog = true;
							break;
						}
					}
				}
				return isLog;
			}

			/**
			 * decide if this field need be traced
			 * 
			 * @param fieldaccess
			 * @return
			 */
			// private boolean isNeedLog(FieldAccess fieldaccess) {
			// // not static field
			// if (fieldaccess.isStatic()) {
			// return false;
			// }
			// CtField ctField;
			// try {
			// ctField = fieldaccess.getField();
			// } catch (NotFoundException e1) {
			// return false;
			// }
			// CtClass ctClass;
			// try {
			// ctClass = ctField.getType();
			// } catch (NotFoundException e) {
			// return false;
			// }
			// return isNeedLog(ctClass);
			// }

			/**
			 * decide if this field need be traced
			 * 
			 * @param fieldaccess
			 * @return
			 */
			private boolean isNeedLog(MethodCall methodcall) {
				// not static field
				return isNeedLog(methodcall.getClassName(),
						methodcall.getMethodName());
			}

		});
	}

	private static boolean isCanTrace(Class<?> clazz) {
		if (clazz.isArray()) {
			return false;
		} else {
			return true;
		}
	}

	public static void buildTraceClass(Class<?> clazz) throws Exception {
		if (!isCanTrace(clazz)) {
			return;
		}
		long time = System.currentTimeMillis();
		ClassGenerator classGenerator = ClassGenerator.newInstance(
				Helper.makeClassName(clazz), NativeHelper.getClassBytes(clazz));
		buildClass(classGenerator);
		buildFieldAccess(classGenerator);

		byte[] classBytes = classGenerator.toBytecode();
		// System.out.println("buildTraceClass " + clazz.getName() + ","
		// + classBytes);
		Agent.redefineClass(clazz, classBytes);
		Agent.println("build trace " + clazz.getName() + " use "
				+ (System.currentTimeMillis() - time) + " ms!");
	}

	public static void buildMockClass(Class<?> clazz) throws Exception {
		if (!isCanTrace(clazz)) {
			return;
		}
		ClassGenerator classGenerator = ClassGenerator.newInstance(
				Helper.makeClassName(clazz), NativeHelper.getClassBytes(clazz));
		buildClass(classGenerator);
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
