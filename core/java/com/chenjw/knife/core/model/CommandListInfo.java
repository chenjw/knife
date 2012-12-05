package com.chenjw.knife.core.model;

import java.io.Serializable;

public class CommandListInfo implements Serializable {

	private static final long serialVersionUID = -1045701725746865328L;
	private String[] commandNames;

	public String[] getCommandNames() {
		return commandNames;
	}

	public void setCommandNames(String[] commandNames) {
		this.commandNames = commandNames;
	}

}
