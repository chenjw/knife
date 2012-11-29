package com.chenjw.knife.core.model;

public class InstanceListInfo {
	private String className;
	private ObjectInfo[] instances;

	public ObjectInfo[] getInstances() {
		return instances;
	}

	public void setInstances(ObjectInfo[] instances) {
		this.instances = instances;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}
