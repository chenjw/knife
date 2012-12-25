package com.chenjw.knife.core.model;

public class Command extends Request {
	private static final long serialVersionUID = -5652970762701374531L;
	private String name;
	private Object args;

	public Command() {
	}

	public Command(String name, Object args) {
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

	@Override
	public String toString() {
		return name + " " + args;
	}

}
