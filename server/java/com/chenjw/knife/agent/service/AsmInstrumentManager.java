package com.chenjw.knife.agent.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import net.sf.cglib.asm.Attribute;
import net.sf.cglib.asm.ClassAdapter;
import net.sf.cglib.asm.ClassReader;
import net.sf.cglib.asm.ClassVisitor;
import net.sf.cglib.asm.ClassWriter;

import com.chenjw.bytecode.javassist.ClassGenerator;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.handler.CdCommandHandler;
import com.chenjw.knife.agent.util.NativeHelper;

public class AsmInstrumentManager implements Lifecycle {
	private static final AsmInstrumentManager INSTANCE = new AsmInstrumentManager();
	static {
		ServiceManager.getInstance().register(INSTANCE);
	}
	private static final String[] CLASS_WHITE_LIST = new String[] {
			"java.lang.reflect.InvocationHandler.invoke",
			"java.lang.reflect.Method.invoke" };

	private static final Class<Profiler> PROFILER_CLASS = Profiler.class;

	private final Set<String> TRACED_METHOD = new HashSet<String>();

	public static AsmInstrumentManager getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) throws IOException {
		ClassReader cr = new ClassReader(
				NativeHelper.getClassBytes(CdCommandHandler.class));
		ClassWriter cw = new ClassWriter(true);
		ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);

		cr.accept(classAdapter, true);
		byte[] data = cw.toByteArray();
		File file = new File("/tmp/knife/Account.class");
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(data);
		fout.close();
	}

	public static class AddSecurityCheckClassAdapter extends ClassAdapter {

		public AddSecurityCheckClassAdapter(ClassVisitor arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		// @Override
		// public void visit(int i, int j, String s, String s1, String[] as,
		// String s2) {
		// System.out.println(i + " " + j + " " + s + " " + s1 + " "
		// + StringHelper.join(as, ",") + " " + s2);
		// super.visit(i, j, s, s1, as, s2);
		// }

		@Override
		public void visitAttribute(Attribute attribute) {
			System.out.println(attribute);
			super.visitAttribute(attribute);
		}

	}

	private void buildMethodAccess(final ClassGenerator classGenerator,
			final String methodName) throws Exception {
		CtMethod[] ctMethods = classGenerator.getCtClass().getMethods();
		for (final CtMethod method : ctMethods) {
			if (!methodName.equals(method.getName())) {
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
			ClassGenerator newClassGenerator = ClassGenerator
					.newInstance(ByteCodeManager.getInstance().getByteCode(
							Helper.findClass(classGenerator.getCtClass())));
			CtClass ctClass = newClassGenerator.getCtClass();
			CtMethod newMethod = ctClass.getMethod(method.getName(),
					method.getSignature());
			newMethod.instrument(new MethodCallExprEditor());
			// add enter leave code
			// addEnterLeaveCode(ctClass, newMethod);
			byte[] classBytes = newClassGenerator.toBytecode();
			ByteCodeManager.getInstance().tryRedefineClass(
					Helper.findClass(newClassGenerator.getCtClass()),
					classBytes);
		}

	}

	private void addEnterLeaveCode(CtClass ctClass, CtMethod ctMethod) {
		try {
			// ctMethod.getMethodInfo().
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

				ctMethod.insertBefore("{" + PROFILER_CLASS.getName()
						+ ".enter(null,\""
						+ Helper.makeClassName(Helper.findClass(ctClass))
						+ "\",\"" + ctMethod.getName() + "\",$args);}");

				ctMethod.insertAfter(
						"{" + PROFILER_CLASS.getName() + ".leave(null,\""
								+ Helper.findClass(ctClass).getName() + "\",\""
								+ ctMethod.getName() + "\",$args," + resultExpr
								+ ");}", true);
			} else {
				ctMethod.insertBefore("{" + PROFILER_CLASS.getName()
						+ ".enter($0,\"" + Helper.findClass(ctClass).getName()
						+ "\",\"" + ctMethod.getName() + "\",$args);}");
				ctMethod.insertAfter(
						"{"
								+ PROFILER_CLASS.getName()
								+ ".leave($0,\""
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

	public void buildTraceMethod(Class<?> clazz, String methodName)
			throws Exception {
		if (!isCanTrace(clazz)) {
			return;
		}
		byte[] bytes = ByteCodeManager.getInstance().getByteCode(clazz);
		ClassGenerator classGenerator = ClassGenerator.newInstance(bytes);
		buildMethodAccess(classGenerator, methodName);
		ByteCodeManager.getInstance().commitAll();
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
					+ className + "\",\"" + methodName + "\",$args,\""
					+ methodcall.getFileName() + "\","
					+ methodcall.getLineNumber() + ");";
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
	}
}
