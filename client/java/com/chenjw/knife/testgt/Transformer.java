package com.chenjw.knife.testgt;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;

public class Transformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		// System.out.println(className + " " + classBeingRedefined);
		ClassReader cr = new ClassReader(classfileBuffer);
		cr.accept(new MyClassVisitor(), 0);

		return null;
	}
}
