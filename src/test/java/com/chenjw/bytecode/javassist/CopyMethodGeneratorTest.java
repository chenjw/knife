package com.chenjw.bytecode.javassist;

import com.chenjw.bytecode.javassist.method.CopyMethodGenerator;
import com.chenjw.knife.test.impl.TestServiceImpl;

public class CopyMethodGeneratorTest {
	public void proxy() {
		ClassGenerator classGenerator = ClassGenerator
				.newInstance("com.chenjw.bytecode.javassist.TestPojo");
		CopyMethodGenerator methodGenerator = CopyMethodGenerator.newInstance(
				classGenerator, "test1");

		classGenerator.addMethod(methodGenerator);
		System.out.println(classGenerator.toClass());
	}

	public static void main(String[] args) {
		TestServiceImpl p = new TestServiceImpl();
		// p.test1(null, null);
		CopyMethodGeneratorTest test = new CopyMethodGeneratorTest();
		test.proxy();

		// p.test1(null, null);
	}
}
