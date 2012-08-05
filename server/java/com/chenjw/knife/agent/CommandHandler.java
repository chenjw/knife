package com.chenjw.knife.agent;

import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;

public interface CommandHandler {
	public void declareArgs(ArgDef argDef);

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception;

}
