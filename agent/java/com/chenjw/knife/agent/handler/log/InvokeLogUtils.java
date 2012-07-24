package com.chenjw.knife.agent.handler.log;

import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.apache.commons.collections.set.SynchronizedSet;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.utils.IntervalHelper;

public class InvokeLogUtils {
	@SuppressWarnings("unchecked")
	private static final Set<String> TRACED_METHOD = SynchronizedSet
			.decorate(new HashSet<String>());

	private static final String[] CLASS_WHITE_LIST = new String[] {
			"java.lang.reflect.InvocationHandler.invoke",
			"java.lang.reflect.Method.invoke" };

	public static void clear() {
		Agent.clear();
		TRACED_METHOD.clear();
	}

	private static void buildMethodAccess(final ClassGenerator classGenerator,
			final String methodName) throws Exception {
		String methodFullName = classGenerator.getCtClass().getName() + "."
				+ methodName;
		// filter traced method
		if (TRACED_METHOD.contains(methodFullName)) {
			return;
		} else {
			TRACED_METHOD.add(methodFullName);
		}
		for (CtMethod method : classGenerator.getCtClass().getMethods()) {

			// filter trace method
			if (!methodName.equals(method.getName())) {
				System.out.println(methodName + " " + method.getName());
				continue;
			}
			// filter unsupport method
			if (!isSupportTrace(classGenerator.getCtClass(), method)) {
				continue;
			}

			method.instrument(new ExprEditor() {
				public void edit(MethodCall methodcall)
						throws CannotCompileException {

					IntervalHelper.start(methodcall.getClassName() + "."
							+ methodcall.getMethodName());

					String proxyCode = InvokeLog.class.getName()
							+ ".traceObject($0,\"" + methodcall.getMethodName()
							+ "\");";

					if ("java.lang.reflect.Method".equals(methodcall
							.getClassName())
							&& "invoke".equals(methodcall.getMethodName())) {

						proxyCode = InvokeLog.class.getName()
								+ ".traceObject($1,\""
								+ methodcall.getMethodName() + "\");";
					}
					if (isStatic(methodcall)) {
						proxyCode = InvokeLog.class.getName()
								+ ".traceClass($class,\""
								+ methodcall.getMethodName() + "\");";
					}
					String startCode = InvokeLog.class.getName()
							+ ".start($0,\"" + methodcall.getClassName()
							+ "\",\"" + methodcall.getMethodName()
							+ "\",$args);";
					String returnEndCode = InvokeLog.class.getName()
							+ ".returnEnd( $0,\"" + methodcall.getClassName()
							+ "\",\"" + methodcall.getMethodName()
							+ "\",$args,($w)$_);";

					String exceptionEndCode = InvokeLog.class.getName()
							+ ".exceptionEnd( $0,\""
							+ methodcall.getClassName() + "\",\""
							+ methodcall.getMethodName() + "\",$args,$e);";
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
					IntervalHelper.printMillis(
							Agent.printer,
							methodcall.getClassName() + "."
									+ methodcall.getMethodName());
					IntervalHelper.start("replace");
					methodcall.replace(code);
					IntervalHelper.printMillis(Agent.printer, "replace");

				}

				private boolean isStatic(MethodCall methodcall) {
					try {
						return Modifier.isStatic(methodcall.getMethod()
								.getModifiers());
					} catch (NotFoundException e) {
						return false;
					}
				}

				// @Override
				// public void edit(FieldAccess fieldaccess)
				// throws CannotCompileException {
				//
				// // if (!isNeedLog(fieldaccess)) {
				// // return;
				// // }
				// // try {
				// // fieldaccess.replace("$_=$proceed($$);"
				// // + InvokeLog.class.getName() + ".proxy(($w)$_);");
				// // } catch (CannotCompileException e) {
				// // Agent.println(fieldaccess.getFieldName() +
				// // " proxy error");
				// //
				// // throw e;
				// // }
				//
				// }

				/**
				 * decide if this field need be traced
				 * 
				 * @param fieldaccess
				 * @return
				 */

			});
		}

	}

	private static boolean isCanTrace(Class<?> clazz) {
		if (clazz.isArray()) {
			return false;
		} else {
			return true;
		}
	}

	public static void buildTraceMethod(Class<?> clazz, String methodName)
			throws Exception {
		if (!isCanTrace(clazz)) {
			return;
		}
		IntervalHelper.start("getClassBytes");
		byte[] bytes = NativeHelper.getClassBytes(clazz);
		IntervalHelper.printMillis(Agent.printer, "getClassBytes");
		IntervalHelper.start("ClassGenerator.newInstance");
		ClassGenerator classGenerator = ClassGenerator.newInstance(
				Helper.makeClassName(clazz), bytes);
		IntervalHelper.printMillis(Agent.printer, "ClassGenerator.newInstance");
		IntervalHelper.start("buildField");
		buildMethodAccess(classGenerator, methodName);
		IntervalHelper.printMillis(Agent.printer, "buildField");
		IntervalHelper.start("toBytecode");
		byte[] classBytes = classGenerator.toBytecode();
		IntervalHelper.printMillis(Agent.printer, "toBytecode");
		IntervalHelper.start("redefine");
		Agent.redefineClass(clazz, classBytes);
		IntervalHelper.printMillis(Agent.printer, "redefine");
	}

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

	private static boolean isSupportTrace(CtClass ctClass, CtMethod ctMethod) {
		// filter name
		boolean isLog = true;
		if (ctClass.getName().startsWith("java.")) {
			isLog = false;
		} else if (ctClass.getName().startsWith("javax.")) {
			isLog = false;
		} else if (ctClass.getName().startsWith("sun.")) {
			isLog = false;
		}
		String name = ctClass.getName() + "." + ctMethod.getName();
		// pass for white list
		if (!isLog) {
			for (String cn : CLASS_WHITE_LIST) {
				if (name.equals(cn)) {
					isLog = true;
					break;
				}
			}
		}
		if (isLog == false) {
			return false;
		}
		// filter native
		if (Modifier.isNative(ctMethod.getModifiers())) {
			return false;
		} else {
			return true;
		}
	}
}
