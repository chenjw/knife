package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.service.HistoryService;
import com.chenjw.knife.agent.service.ObjectRecordService;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;

public class CdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if ("..".equals(param)) {
			obj = HistoryService.getInstance().pre();
		} else {
			int index = Integer.parseInt(param);
			obj = HistoryService.getInstance().cd(index);
		}
		Agent.sendResult(ResultHelper.newStringResult("into "
				+ ObjectRecordService.getInstance().toId(obj)
				+ ToStringHelper.toString(obj)));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setDefinition("cd <object-id>");
	}
}