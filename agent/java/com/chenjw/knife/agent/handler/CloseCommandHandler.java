package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.log.InvokeLog;

public class CloseCommandHandler implements CommandHandler {
	public String getName() {
		return "close";
	}

	@Override
	public void handle(String[] args) {
		InvokeLog.clear();
		Agent.close();
	}

}
