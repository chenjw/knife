package com.chenjw.knife.agent.args;

public class OptionDesc {
	private boolean isOptional;
	private String name;
	private String fullName;
	private String[] valueDefs;
	private String desc;

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String[] getValueDefs() {
		return valueDefs;
	}

	public void setValueDefs(String[] value) {
		this.valueDefs = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
