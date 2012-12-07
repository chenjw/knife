package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;

public class LogCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("log");
		argDef.setDesc("not support yet。");
	}
}
