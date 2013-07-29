package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.Command;

public abstract class CommandListenerTemplate extends CommandSenderTemplate
		implements CommandListener {

	protected ResponseCallback callBack;

	public void onCommand(Command command) {
		this.sendCommand(command, callBack);
	}

}
