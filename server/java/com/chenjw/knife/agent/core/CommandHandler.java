package com.chenjw.knife.agent.core;

import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;

public interface CommandHandler {
	public void declareArgs(ArgDef argDef);

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception;

}
