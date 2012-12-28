package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.testgt.ResponseCallback;

public abstract class DefaultCommandListener extends DefaultCommandSender
		implements CommandListener {

	protected ResponseCallback callBack;

	public void onCommand(Command command) {
		this.sendCommand(command, callBack);
	}

}
