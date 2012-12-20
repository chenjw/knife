package com.chenjw.knife.core.model;

import java.io.Serializable;

public class Command implements Serializable {
	private static final long serialVersionUID = -5652970762701374531L;
	private String name;
	private String args;

	public Command() {
	}

	public Command(String name, String args) {
		this.name = name;
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	@Override
	public String toString() {
		return name + " " + args;
	}

}
