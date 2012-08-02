package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.log.InvokeHistory;

public class CdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if ("..".equals(param)) {
			obj = InvokeHistory.pre();
		} else {
			int index = Integer.parseInt(param);
			obj = InvokeHistory.cd(index);
		}
		Agent.println("into " + obj);

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("cd");
		argDef.setDef("<object-id>");
		argDef.setDesc("enter object by id.");
		argDef.addOptionDesc("object-id", "a num as the object id.");
	}
}
