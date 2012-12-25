package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.HistoryService;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class CdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if ("..".equals(param)) {
			obj = ServiceRegistry.getService(HistoryService.class).pre();
		} else {
			int index = Integer.parseInt(param);
			obj = ServiceRegistry.getService(HistoryService.class).cd(index);
		}
		Agent.sendResult(ResultHelper.newStringResult("into "
				+ ServiceRegistry.getService(ObjectHolderService.class).toId(
						obj) + ToStringHelper.toString(obj)));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setDefinition("cd <object-id>");
	}
}