package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.handler.constants.Constants;

public class CdCommandHandler implements CommandHandler {

	public void handle(String[] args) {
		try {
			String param = args[0];
			int index = Integer.parseInt(param);
			Object[] objs = (Object[]) Context.get(Constants.LIST);
			Object obj = objs[index];
			Context.put(Constants.THIS, obj);
			Agent.print("into " + index + ". " + obj);
		} catch (Exception e) {
			Agent.print(e.getClass().getName() + ":" + e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "cd";
	}
}
