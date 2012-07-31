package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;

public class LogCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("log");
		argDef.setDesc("not support yet.");
	}
}
