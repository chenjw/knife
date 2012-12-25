package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class PropCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		if ("debug".equals(args.arg("type"))) {
			if ("on".equals(args.arg("status"))) {
				Agent.getAgentInfo().setDebugOn(true);
			} else {
				Agent.getAgentInfo().setDebugOn(false);
			}
		} else if ("language".equals(args.arg("type"))) {
			dispatcher.setDescLanguage(args.arg("status"));
		}
		Agent.sendResult(ResultHelper.newStringResult("finished!"));
	}

	@Override
	public void declareArgs(ArgDef argDef) {
		argDef.setDefinition("prop <type> <status>");
	}

}
