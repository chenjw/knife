package com.chenjw.knife.agent.core;

import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public interface CommandHandler {
	public void declareArgs(ArgDef argDef);

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception;

}
