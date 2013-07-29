package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class CommandListInfo implements Serializable {

	private static final long serialVersionUID = 1650208876081096749L;
	private CommandInfo[] commands;

	public CommandInfo[] getCommands() {
		return commands;
	}

	public void setCommands(CommandInfo[] commands) {
		this.commands = commands;
	}

}
