package com.chenjw.knife.agent.handler;

import java.util.Set;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.core.model.CommandListInfo;
import com.chenjw.knife.core.result.Result;

public class CmdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		Set<String> cmdNames=dispatcher.getArgDefMap().keySet();
		CommandListInfo info=new CommandListInfo();
		info.setCommandNames(cmdNames.toArray(new String[cmdNames.size()]));
		Result<CommandListInfo> result = new Result<CommandListInfo>();
		result.setContent(info);
		result.setSuccess(true);
		Agent.sendResult(result);
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("cmd");
		argDef.setDesc("更新客户端暂存的服务端命令列表.");
	}
}