package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.service.ServiceManager;

public class CloseCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		ServiceManager.getInstance().clear();
		Agent.close();
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("close");
		argDef.setDesc("close the agent from client.");
	}

}
