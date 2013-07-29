package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.Command;

public interface CommandSender {
	public void sendCommand(Command command, ResponseCallback callback);

	public ResultModel sendSyncCommand(Command command);
}
