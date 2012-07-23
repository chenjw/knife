package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.InvokeRecord;

public class CdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			String param = args.arg(0);
			int index = Integer.parseInt(param);
			Object obj = InvokeRecord.get(index);
			Context.put(Constants.THIS, obj);
			Agent.println("into " + obj);
		} catch (Exception e) {
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "cd";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecs) {
	}
}
