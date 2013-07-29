package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class CommandInfo implements Serializable {

	private static final long serialVersionUID = -7438568861350606795L;
	private String name;
	private Object args;

	public CommandInfo(String name, Object args) {
		this.name = name;
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getArgs() {
		return args;
	}

	public void setArgs(Object args) {
		this.args = args;
	}

}
