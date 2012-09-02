package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.manager.HistoryManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.ToStringHelper;

public class CdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if ("..".equals(param)) {
			obj = HistoryManager.getInstance().pre();
		} else {
			int index = Integer.parseInt(param);
			obj = HistoryManager.getInstance().cd(index);
		}
		Agent.info("into " + ObjectRecordManager.getInstance().toId(obj)
				+ ToStringHelper.toString(obj));

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("cd");
		argDef.setDef("<object-id>");
		argDef.setDesc("enter object by id.");
		argDef.addOptionDesc("object-id", "a num as the object id.");
	}
}
