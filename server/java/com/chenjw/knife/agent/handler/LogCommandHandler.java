package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class LogCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		Agent.sendResult(ResultHelper.newResult("log finished!"));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setDefinition("log");

	}
}
