package com.chenjw.knife.agent.handler.log;

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

import org.apache.commons.collections.set.SynchronizedSet;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Helper;

public class TraceCodeBuilder {
	@SuppressWarnings("unchecked")
	private static final Set<String> TRACED_METHOD = SynchronizedSet
			.decorate(new HashSet<String>());

	private static final String[] CLASS_WHITE_LIST = new String[] {
			"java.lang.reflect.InvocationHandler.invoke",
			"java.lang.reflect.Method.invoke" };

	private static final Class<Profiler> PROFILER_CLASS = Profiler.class;

	public static void clear() {
		ByteCodeManager.getInstance().recoverAll();
		TRACED_METHOD.clear();
	}

	private static void buildMethodAccess(final ClassGenerator classGenerator,
			final String methodName) throws Exception {
		// System.out.println("into class "
		// + classGenerator.getCtClass().getName());
		// TimingHelper.start("getMethods");
		CtMethod[] ctMethods = classGenerator.getCtClass().getMethods();
		// TimingHelper.printMillis("getMethods");
		for (final CtMethod method : ctMethods) {
			// System.out.println("find method "
			// + method.getDeclaringClass().getName() + "."
			// + method.getName());
			// filter trace method
			// TimingHelper.start("check");
			if (!methodName.equals(method.getName())) {
				// System.out.println(methodName + " " + method.getName());
				continue;
			}
			String methodFullName = method.getLongName();
			// filter traced method
			if (TRACED_METHOD.contains(methodFullName)) {
				return;
			} else {
				TRACED_METHOD.add(methodFullName);
			}
			// filter unsupport method
			if (!isSupportTrace(method.getDeclaringClass().getName(), method)) {
				continue;
			}
			// TimingHelper.printMillis("check");
			// TimingHelper.start("new1");
			ClassGenerator newClassGenerator = ClassGenerator
					.newInstance(ByteCodeManager.getInstance().getByteCode(
							Helper.findClass(classGenerator.getCtClass())));
			// TimingHelper.printMillis("new1");
			// TimingHelper.start("getMethod");
			CtClass ctClass = newClassGenerator.getCtClass();
			CtMethod newMethod = ctClass.getMethod(method.getName(),
					method.getSignature());
			// TimingHelper.printMillis("getMethod");
			// TimingHelper.start("replace");
			newMethod.instrument(new MethodCallExprEditor());
			// ////////////////
			Class<?> returnClass = null;
			try {
				returnClass = Helper.findClass(newMethod.getReturnType());
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
			//
			// // /////////
			if (Modifier.isStatic(method.getModifiers())) {
				newMethod.insertBefore("{"
						+ PROFILER_CLASS.getName()
						+ ".enter(null,\""
						+ Helper.makeClassName(Helper
								.findClass(newClassGenerator.getCtClass()))
						+ "\",\"" + method.getName() + "\",$args);}");
				newMethod.insertAfter(
						"{"
								+ PROFILER_CLASS.getName()
								+ ".leave(null,\""
								+ Helper.findClass(
										newClassGenerator.getCtClass())
										.getName() + "\",\"" + method.getName()
								+ "\",$args," + resultExpr + ");}", true);
			} else {
				newMethod.insertBefore("{"
						+ PROFILER_CLASS.getName()
						+ ".enter($0,\""
						+ Helper.findClass(newClassGenerator.getCtClass())
								.getName() + "\",\"" + method.getName()
						+ "\",$args);}");
				newMethod.insertAfter(
						"{"
								+ PROFILER_CLASS.getName()
								+ ".leave($0,\""
								+ Helper.makeClassName(Helper
										.findClass(newClassGenerator
												.getCtClass())) + "\",\""
								+ method.getName() + "\",$args," + resultExpr
								+ ");}", true);
			}
			// ///////////////////
			// //
			// newMethod.insertBefore("System.out.println(\"insertBefore\");");
			// newMethod.insertAfter("{System.out.println(\"insertAfter\");}");
			//
			// //

			// System.out.println("newMethod.insertAfter " +
			// newMethod.getName());
			// newMethod.insertAfter("{System.out.println(\"insertAfter\");}",
			// true);

			// String newMethodName = "_aaa" + methodName;
			// newMethod.setName(newMethodName);
			// // newMethod.setName(newMethodName);
			// CtMethod nnMethod = CtNewMethod.copy(newMethod, methodName,
			// ctClass, null);
			// nnMethod.setModifiers(newMethod.getModifiers());
			// System.out.println("getDeclaringClass=");
			// // CtNewMethod.copy(src, declaring, map)
			// // newMethod.setName(newMethodName);
			// nnMethod.setBody("{System.out.println(\"call\");return "
			// + newMethodName + "($$);}");
			// //
			// nnMethod.setModifiers(Modifier.setPrivate(nnMethod.getModifiers()));
			// // nnMethod.instrument(new MethodCallExprEditor());

			// // nnMethod.
			// ctClass.addMethod(nnMethod);

			// newMethod.instrument(new FieldAccessExprEditor());
			// newMethod.setBody("{System.out.println(\"enter\");return _a_"
			// + newMethod.getName() + "($$);}");

			// TimingHelper.printMillis("replace");
			// TimingHelper.start("toBytecode");

			byte[] classBytes = newClassGenerator.toBytecode();
			// TimingHelper.printMillis("toBytecode");
			// TimingHelper.start("tryRedefineClass");
			ByteCodeManager.getInstance().tryRedefineClass(
					Helper.findClass(newClassGenerator.getCtClass()),
					classBytes);
			// TimingHelper.printMillis("tryRedefineClass");
		}

	}

	private static boolean isCanTrace(Class<?> clazz) {
		if (clazz.isArray()) {
			return false;
		} else if (clazz.isInterface()) {
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
		// TimingHelper.start("build " + clazz.getName() + "." + methodName);
		// TimingHelper.start("getByteCode " + clazz.getName() + "." +
		// methodName);
		byte[] bytes = ByteCodeManager.getInstance().getByteCode(clazz);
		// TimingHelper.printMillis("getByteCode " + clazz.getName() + "."
		// + methodName);
		// TimingHelper.start("new " + clazz.getName() + "." + methodName);
		ClassGenerator classGenerator = ClassGenerator.newInstance(bytes);
		// TimingHelper.printMillis("new " + clazz.getName() + "." +
		// methodName);
		// TimingHelper.start("buildMethodAccess " + clazz.getName() + "."
		// + methodName);
		buildMethodAccess(classGenerator, methodName);
		// TimingHelper.printMillis("buildMethodAccess " + clazz.getName() + "."
		// + methodName);
		// TimingHelper.printMillis("build " + clazz.getName() + "." +
		// methodName);
		// TimingHelper.start("commit " + clazz.getName() + "." + methodName);
		ByteCodeManager.getInstance().commitAll();
		// TimingHelper
		// .printMillis("commit " + clazz.getName() + "." + methodName);
	}

	private static boolean isSupportClassNameAndMethodName(String className,
			String methodName) {
		// filter name
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

	private static boolean isSupportTrace(String className, CtMethod ctMethod) {
		try {
			// TimingHelper.start("check support");
			// filter name
			if (!isSupportClassNameAndMethodName(className, ctMethod.getName())) {
				return false;
			}
			// filter native
			if (Modifier.isNative(ctMethod.getModifiers())) {
				return false;
			} else {
				return true;
			}
		} finally {
			// TimingHelper.printMillis("check support");
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

	public static class MethodCallExprEditor extends ExprEditor {

		public void edit(MethodCall methodcall) throws CannotCompileException {
			String className = methodcall.getClassName();
			String methodName = methodcall.getMethodName();
			CtMethod ctMethod = null;
			try {
				ctMethod = methodcall.getMethod();
			} catch (NotFoundException e1) {
				throw new RuntimeException(methodName + " not found!", e1);
			}
			if (!isSupportTrace(className, ctMethod)) {
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
				proxyCode = PROFILER_CLASS.getName() + ".traceClass($class,\""
						+ methodName + "\");";
			} else if ("java.lang.reflect.Method".equals(className)
					&& "invoke".equals(methodName)) {
				proxyCode = PROFILER_CLASS.getName()
						+ ".traceObject($1,$0.getName());";
			} else {
				proxyCode = PROFILER_CLASS.getName() + ".traceObject($0,\""
						+ methodName + "\");";
			}

			String startCode = PROFILER_CLASS.getName() + ".start($0,\""
					+ className + "\",\"" + methodName + "\",$args);";
			String returnEndCode = PROFILER_CLASS.getName()
					+ ".returnEnd( $0,\"" + className + "\",\"" + methodName
					+ "\",$args," + resultExpr + ");";

			String exceptionEndCode = PROFILER_CLASS.getName()
					+ ".exceptionEnd( $0,\"" + className + "\",\"" + methodName
					+ "\",$args,$e);";
			StringBuffer code = new StringBuffer("try{");
			code.append(startCode);
			code.append(proxyCode);
			code.append("$_ = $proceed($$);");
			code.append(returnEndCode);
			code.append("}");
			code.append("catch(java.lang.Throwable $e){");
			code.append(exceptionEndCode);
			code.append("throw $e;");
			code.append("}");
			// TimingHelper.start("doReplace " + className + "." + methodName);
			methodcall.replace(code.toString());
			// TimingHelper.printMillis("doReplace " + className + "."
			// + methodName);
		}

		private boolean isStatic(CtMethod ctMethod) {
			return Modifier.isStatic(ctMethod.getModifiers());
		}

	}
}
