package com.chenjw.knife.core.model;

public class ClassInfo {
	private boolean isInterface;
	private String name;
	private String classLoader;
	public boolean isInterface() {
		return isInterface;
	}
	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassLoader() {
		return classLoader;
	}
	public void setClassLoader(String classLoader) {
		this.classLoader = classLoader;
	}
		

}
