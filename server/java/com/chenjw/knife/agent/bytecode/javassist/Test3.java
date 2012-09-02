package com.chenjw.knife.agent.bytecode.javassist;

import java.lang.reflect.Method;

public class Test3 extends Test2 {
	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException {
		Method m = Test3.class.getDeclaredMethod("test", new Class<?>[0]);

		System.out.println(m.getDeclaringClass() + " " + m.getName());

		System.out.println("------------");
		m = Test2.class.getDeclaredMethod("test", new Class<?>[0]);
		System.out.println(m.getDeclaringClass() + " " + m.getName());

	}

	@Override
	protected void test() {
		// TODO Auto-generated method stub
		super.test();
	}

}
