package com.chenjw.knife.testgt;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.chenjw.knife.utils.ClassHelper;

public class MyClassVisitor extends ClassVisitor {
	private TestClassInfo testClassInfo = new TestClassInfo();

	@Override
	public AnnotationVisitor visitAnnotation(String className, boolean arg1) {

		if (ClassHelper.findClass(className) == TestClass.class) {
			System.out.println(className);
		}

		return super.visitAnnotation(className, arg1);
	}

	public MyClassVisitor() {
		super(Opcodes.ASM4);
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
	}

	@Override
	public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
			String arg3, String[] arg4) {
		// TODO Auto-generated method stub
		return super.visitMethod(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public void visit(int i, int j, String s, String s1, String s2, String[] as) {

	}

	@Override
	public FieldVisitor visitField(int i, String fieldName, String fieldClass,
			String s2, Object obj) {

		return new MyFieldVisitor();
	}

	@Override
	public void visitSource(String arg0, String arg1) {

	}

}
