package com.chenjw.knife.testgt;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class MyFieldVisitor extends FieldVisitor {

	public MyFieldVisitor() {
		super(Opcodes.ASM4);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String s, boolean flag) {
		System.out.println(s);
		return super.visitAnnotation(s, flag);
	}
}
