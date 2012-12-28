package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.Command;

public interface CommandListener {
	public void onCommand(Command command);
}
