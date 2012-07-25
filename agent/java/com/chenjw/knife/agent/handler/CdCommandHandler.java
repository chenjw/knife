package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.log.InvokeHistory;

public class CdCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			String param = args.arg(0);
			Object obj = null;
			if ("..".equals(param)) {
				obj = InvokeHistory.pre();
			} else {
				int index = Integer.parseInt(param);
				obj = InvokeHistory.cd(index);
			}
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
