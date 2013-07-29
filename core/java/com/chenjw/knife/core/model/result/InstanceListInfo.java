package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class InstanceListInfo implements Serializable {

	private static final long serialVersionUID = -4684528813449974679L;
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
