package com.chenjw.knife.agent.bytecode.javassist;

import javassist.CtClass;
import javassist.bytecode.ConstPool;

public class Test2 implements Test1 {

	protected void test() {

	}

	public static void main(String[] args) {
		ClassGenerator cg = ClassGenerator.newInstance(ClassGenerator.class
				.getName(), new ClassLoaderClassPath(Thread.currentThread()
				.getContextClassLoader()));
		CtClass c = cg.getCtClass();
		ConstPool cp = c.getClassFile().getConstPool();

		for (int i = 0; i < cp.getSize(); i++) {
			Object obj = cp.getLdcValue(i);
			if (obj != null) {
				System.out.println(i + " " + obj);
			}
		}

	}
}
