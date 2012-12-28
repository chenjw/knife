package com.chenjw.knife.agent.service;

import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.core.model.Command;

public class CommandStatusService implements Lifecycle {

	private Command currentCommand = null;
	private boolean waiting = false;

	public void setCurrentCommand(Command command) {
		this.currentCommand = command;
	}

	public Command getCurrentCommand() {
		return currentCommand;
	}

	public void waitResult() {
		waiting = true;
	}

	public boolean isWaiting() {
		return waiting;
	}

	@Override
	public void init() {

	}

	@Override
	public void clear() {

	}

	@Override
	public void close() {

	}
}
