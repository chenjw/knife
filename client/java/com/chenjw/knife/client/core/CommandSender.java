package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.testgt.ResponseCallback;

public interface CommandSender {
	public void sendCommand(Command command, ResponseCallback resulthandler);

	public ResultModel sendSyncCommand(Command command);
}
