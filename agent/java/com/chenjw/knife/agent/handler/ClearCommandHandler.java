package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.service.ServiceManager;

public class ClearCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {

		ServiceManager.getInstance().clear();
		Agent.println("clear finished!");
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("clear");
		argDef.setDesc("clear all the bytecode enhances and object holders. recover the agent to the time it just attached to the jvm.");
	}

}
