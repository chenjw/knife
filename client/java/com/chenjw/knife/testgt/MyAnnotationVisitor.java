package com.chenjw.knife.testgt;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class MyAnnotationVisitor extends AnnotationVisitor {

	public MyAnnotationVisitor() {
		super(Opcodes.ASM4);

	}

	@Override
	public void visit(String s, Object obj) {
		System.out.println(s);
	}

}
