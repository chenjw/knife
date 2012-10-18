package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;

public class GcCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		System.gc();
		Agent.info("gc finished!");
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("gc");
		argDef.setDesc("force jvm gc.");
	}

}
