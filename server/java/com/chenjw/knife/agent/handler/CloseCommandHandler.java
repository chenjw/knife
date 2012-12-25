package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class CloseCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		ServiceRegistry.clear();
		Agent.close();
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("close");
	}

}
