package com.chenjw.knife.agent.handler.log;

import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.apache.commons.collections.set.SynchronizedSet;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;

public class InvokeLogUtils {
	@SuppressWarnings("unchecked")
	private static final Set<String> TRACED_METHOD = SynchronizedSet
			.decorate(new HashSet<String>());

	private static final String[] CLASS_WHITE_LIST = new String[] {
			"java.lang.reflect.InvocationHandler.invoke",
			"java.lang.reflect.Method.invoke" };

	private static final Class<Profiler> PROFILER_CLASS = Profiler.class;

	public static void clear() {
		Agent.clear();
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
							Helper.findClass(method.getDeclaringClass())));
			// TimingHelper.printMillis("new1");
			// TimingHelper.start("getMethod");
			CtMethod newMethod = newClassGenerator.getCtClass().getMethod(
					method.getName(), method.getSignature());
			// TimingHelper.printMillis("getMethod");
			// TimingHelper.start("replace");
			newMethod.instrument(new MethodCallExprEditor());
			// TimingHelper.printMillis("replace");
			// TimingHelper.start("toBytecode");
			byte[] classBytes = newClassGenerator.toBytecode();
			// TimingHelper.printMillis("toBytecode");
			// TimingHelper.start("tryRedefineClass");
			ByteCodeManager.getInstance().tryRedefineClass(
					Helper.findClass(method.getDeclaringClass()), classBytes);
			// TimingHelper.printMillis("tryRedefineClass");
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
					+ "\",$args,($w)$_);";

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
