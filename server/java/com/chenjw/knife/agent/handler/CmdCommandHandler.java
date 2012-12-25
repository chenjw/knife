package com.chenjw.knife.agent.handler;

import java.util.Set;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.CommandListInfo;

public class CmdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		Set<String> cmdNames = dispatcher.getArgDefMap().keySet();
		CommandListInfo info = new CommandListInfo();
		info.setCommandNames(cmdNames.toArray(new String[cmdNames.size()]));
		Agent.sendResult(ResultHelper.newResult(info));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setDefinition("cmd");

	}
}