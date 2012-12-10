package com.chenjw.knife.agent.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.bytecode.javassist.ClassGenerator;
import com.chenjw.knife.agent.bytecode.javassist.ClassLoaderClassPath;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.core.Lifecycle;

public class InstrumentService implements Lifecycle {
	private static final InstrumentService INSTANCE = new InstrumentService();

	private static final String[] CLASS_WHITE_LIST = new String[] {
			"java.lang.reflect.InvocationHandler.invoke",
			"java.lang.reflect.Method.invoke" };

	private static final Class<Profiler> PROFILER_CLASS = Profiler.class;

	private final Set<String> TRACED_METHOD = new HashSet<String>();

	private final Set<String> ENTER_TRACED_METHOD = new HashSet<String>();

	public static InstrumentService getInstance() {
		return INSTANCE;
	}

	private void buildMethodAccess(Method method) throws Exception {
		// System.out.println(InstrumentManager.class.getClassLoader());
		String methodFullName = method.toGenericString();
		// filter traced method
		if (TRACED_METHOD.contains(methodFullName)) {
			return;
		} else {
			TRACED_METHOD.add(methodFullName);
		}
		// filter unsupport method
		if (!isSupportTrace(method.getDeclaringClass().getName(),
				method.getName(), method.getModifiers())) {
			return;
		}

		ClassGenerator newClassGenerator = ClassGenerator.newInstance(method
				.getDeclaringClass().getName(), new ClassLoaderClassPath(
				AgentClassLoader.getAgentClassLoader()));
		CtClass ctClass = newClassGenerator.getCtClass();
		CtMethod newMethod = Helper.findCtMethod(ctClass, method);

		if (newMethod != null) {
			newMethod.instrument(new MethodCallExprEditor());
			// add enter leave code
			// addEnterLeaveCode(ctClass, newMethod);
			byte[] classBytes = newClassGenerator.toBytecode();
			ByteCodeService.getInstance().tryRedefineClass(
					method.getDeclaringClass(), classBytes);
			ByteCodeService.getInstance().commitAll();

		}

	}

	public void buildMethodEnterLeave(Method method) throws Exception {
		String methodFullName = method.toGenericString();
		// filter traced method
		if (ENTER_TRACED_METHOD.contains(methodFullName)) {
			return;
		} else {
			ENTER_TRACED_METHOD.add(methodFullName);
		}
		// filter unsupport method
		if (!isSupportTrace(method.getDeclaringClass().getName(),
				method.getName(), method.getModifiers())) {
			return;
		}
		ClassGenerator newClassGenerator = ClassGenerator.newInstance(method
				.getDeclaringClass().getName(), new ClassLoaderClassPath(
				AgentClassLoader.getAgentClassLoader()));
		CtClass ctClass = newClassGenerator.getCtClass();
		CtMethod newMethod = Helper.findCtMethod(ctClass, method);
		if (newMethod != null) {
			// add enter leave code
			addEnterLeaveCode(ctClass, newMethod);
			byte[] classBytes = newClassGenerator.toBytecode();
			ByteCodeService.getInstance().tryRedefineClass(
					Helper.findClass(newClassGenerator.getCtClass()),
					classBytes);
			ByteCodeService.getInstance().commitAll();
		}

	}

	private void addEnterLeaveCode(CtClass ctClass, CtMethod ctMethod) {
		try {
			// ////////////////
			Class<?> returnClass = null;
			try {
				returnClass = Helper.findClass(ctMethod.getReturnType());
			} catch (NotFoundException e) {
				e.printStackTrace();
			}

			String resultExpr = null;
			if (returnClass == void.class) {
				resultExpr = PROFILER_CLASS.getName() + ".VOID";
			} else {
				resultExpr = "($w)$_";
			}
			//
			// // /////////
			if (Modifier.isStatic(ctMethod.getModifiers())) {

				ctMethod.insertBefore("{" + PROFILER_CLASS.getName() + "."
						+ Profiler.METHOD_NAME_ENTER + "(null,\""
						+ Helper.makeClassName(Helper.findClass(ctClass))
						+ "\",\"" + ctMethod.getName() + "\",$args);}");

				ctMethod.insertAfter(
						"{" + PROFILER_CLASS.getName() + "."
								+ Profiler.METHOD_NAME_LEAVE + "(null,\""
								+ Helper.findClass(ctClass).getName() + "\",\""
								+ ctMethod.getName() + "\",$args," + resultExpr
								+ ");}", true);
			} else {
				ctMethod.insertBefore("{" + PROFILER_CLASS.getName() + "."
						+ Profiler.METHOD_NAME_ENTER + "($0,\""
						+ Helper.findClass(ctClass).getName() + "\",\""
						+ ctMethod.getName() + "\",$args);}");
				ctMethod.insertAfter(
						"{"
								+ PROFILER_CLASS.getName()
								+ "."
								+ Profiler.METHOD_NAME_LEAVE
								+ "($0,\""
								+ Helper.makeClassName(Helper
										.findClass(ctClass)) + "\",\""
								+ ctMethod.getName() + "\",$args," + resultExpr
								+ ");}", true);
			}
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

	private boolean isCanTrace(Class<?> clazz) {
		if (clazz.isArray()) {
			return false;
		} else if (clazz.isInterface()) {
			return false;
		} else {
			return true;
		}
	}

	public void buildTraceMethod(Method method) throws Exception {
		if (!isCanTrace(method.getDeclaringClass())) {
			return;
		}
		buildMethodAccess(method);

	}

	private static boolean isSupportClassNameAndMethodName(String className,
			String methodName) {
		// filter name
		if (className.equals(Profiler.class.getName())) {
			return false;
		}
		boolean isLog = true;
		if (className.startsWith("java.")) {
			isLog = false;
		} else if (className.startsWith("javax.")) {
			isLog = false;
		} else if (className.startsWith("sun.")) {
			isLog = false;
		}

		String name = className + "." + methodName;
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

	private static boolean isSupportTrace(String className, String methodName,
			int methodModifier) {
		// filter name
		if (!isSupportClassNameAndMethodName(className, methodName)) {
			return false;
		}
		// filter native
		if (Modifier.isNative(methodModifier)) {
			return false;
		} else {
			return true;
		}
	}

	public static class FieldAccessExprEditor extends ExprEditor {

		@Override
		public void edit(FieldAccess f) throws CannotCompileException {
			try {
				System.out.println(f.getField().getSignature() + " "
						+ f.getField().getName());
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static class MethodCallExprEditor extends ExprEditor {

		public void edit(MethodCall methodcall) throws CannotCompileException {
			String className = methodcall.getClassName();
			String methodName = methodcall.getMethodName();
			CtMethod ctMethod = null;
			try {
				ctMethod = methodcall.getMethod();
			} catch (NotFoundException e1) {
				throw new RuntimeException(methodName + " not found!", e1);
			}
			if (!isSupportTrace(className, ctMethod.getName(),
					ctMethod.getModifiers())) {
				return;
			}
			Class<?> returnClass = null;
			try {
				returnClass = Helper.findClass(ctMethod.getReturnType());
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String resultExpr = null;
			if (returnClass == void.class) {
				resultExpr = PROFILER_CLASS.getName() + ".VOID";
			} else {
				resultExpr = "($w)$_";
			}
			String proxyCode = null;

			if (isStatic(ctMethod)) {
				proxyCode = PROFILER_CLASS.getName() + "."
						+ Profiler.METHOD_NAME_PROFILE_STATIC_METHOD
						+ "($class,\"" + className + "\",\"" + methodName
						+ "\");";
			} else if ("java.lang.reflect.Method".equals(className)
					&& "invoke".equals(methodName)) {
				proxyCode = PROFILER_CLASS.getName() + "."
						+ Profiler.METHOD_NAME_PROFILE_METHOD
						+ "($1,$0.getDeclaringClass().getName(),$0.getName());";
			} else {
				proxyCode = PROFILER_CLASS.getName() + "."
						+ Profiler.METHOD_NAME_PROFILE_METHOD + "($0,\""
						+ className + "\",\"" + methodName + "\");";
			}

			String startCode = PROFILER_CLASS.getName() + "."
					+ Profiler.METHOD_NAME_START + "($0,\"" + className
					+ "\",\"" + methodName + "\",$args,\""
					+ methodcall.getFileName() + "\","
					+ methodcall.getLineNumber() + ");";
			String returnEndCode = PROFILER_CLASS.getName() + "."
					+ Profiler.METHOD_NAME_RETURN_END + "($0,\"" + className
					+ "\",\"" + methodName + "\",$args," + resultExpr + ");";

			String exceptionEndCode = PROFILER_CLASS.getName() + "."
					+ Profiler.METHOD_NAME_EXCEPTION_END + "($0,\"" + className
					+ "\",\"" + methodName + "\",$args,$e);";
			StringBuffer code = new StringBuffer("try{");
			code.append(startCode);
			code.append(proxyCode);
			code.append("$_=$proceed($$);");
			code.append(returnEndCode);
			code.append("}");
			code.append("catch(java.lang.Throwable $e){");
			code.append(exceptionEndCode);
			code.append("throw $e;");
			code.append("}");

			methodcall.replace(code.toString());
		}

		private boolean isStatic(CtMethod ctMethod) {
			return Modifier.isStatic(ctMethod.getModifiers());
		}

	}

	@Override
	public void init() {

	}

	@Override
	public void clear() {
		TRACED_METHOD.clear();
		ENTER_TRACED_METHOD.clear();
	}

	@Override
	public void close() {

	}
}
