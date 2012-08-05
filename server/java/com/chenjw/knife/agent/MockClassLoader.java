package com.chenjw.knife.agent;

public class MockClassLoader extends ClassLoader {

	public MockClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String s) throws ClassNotFoundException {
		System.out.println("findClass " + s);
		return super.findClass(s);
	}

	@Override
	public Class<?> loadClass(String s) throws ClassNotFoundException {
		System.out.println("loadClass " + s);
		return super.loadClass(s);
	}

	@Override
	protected synchronized Class<?> loadClass(String s, boolean flag)
			throws ClassNotFoundException {
		System.out.println("loadClass " + s);
		return super.loadClass(s, flag);
	}

}
