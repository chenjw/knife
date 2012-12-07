package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;

public class PropCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		if ("debug".equals(args.arg("type"))) {
			if ("on".equals(args.arg("status"))) {
				Agent.getAgentInfo().setDebugOn(true);
			} else {
				Agent.getAgentInfo().setDebugOn(false);
			}
		}
		Agent.sendResult(ResultHelper.newStringResult("finished!"));
	}

	@Override
	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("prop");
		argDef.setDef("<type> <status>");
		argDef.setDesc("一些全局设置信息。");
		argDef.addOptionDesc("type", "debug");
		argDef.addOptionDesc("status", "on/off");

	}

}
