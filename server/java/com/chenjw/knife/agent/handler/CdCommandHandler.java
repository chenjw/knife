package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.manager.HistoryManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.ResultHelper;
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
		Agent.sendResult(ResultHelper.newStringResult("into "
				+ ObjectRecordManager.getInstance().toId(obj)
				+ ToStringHelper.toString(obj)));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("cd");
		argDef.setDef("<object-id>");
		argDef.setDesc("把指定id的对象当作当前对象。");
		argDef.addOptionDesc("object-id", "目标对象的id。");
	}
}