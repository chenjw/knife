package com.chenjw.knife.testgt;

import com.chenjw.knife.core.model.Command;

public interface TestgtService {
	public void sendCommand(Command command, ResultHandler resulthandler);
}
