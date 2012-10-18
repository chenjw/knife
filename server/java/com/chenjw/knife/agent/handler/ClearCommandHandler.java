package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.manager.Registry;

public class ClearCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {

		Registry.getInstance().clear();
		Agent.info("clear finished!");
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("clear");
		argDef.setDesc("clear all the bytecode enhances and object holders. recover the agent to the time it just attached to the jvm.");
	}

}
