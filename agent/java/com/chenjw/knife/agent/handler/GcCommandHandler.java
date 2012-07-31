package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;

public class GcCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		System.gc();
		Agent.println("gc finished!");
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("gc");
		argDef.setDesc("force jvm gc.");
	}

}
