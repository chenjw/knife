package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.log.InvokeLog;

public class ClearCommandHandler implements CommandHandler {

	@Override
	public void handle(String[] args) {
		InvokeLog.clear();
	}

	@Override
	public String getName() {
		return "clear";
	}

}
