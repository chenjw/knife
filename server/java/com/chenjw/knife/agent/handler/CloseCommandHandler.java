package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.manager.Registry;

public class CloseCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		Registry.getInstance().clear();
		Agent.close();
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("close");
		argDef.setDesc("close the agent from client.");
	}

}
