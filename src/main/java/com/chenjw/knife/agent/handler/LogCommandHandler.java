package com.chenjw.knife.agent.handler;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.log.InvokeLog;
import com.chenjw.knife.agent.handler.log.InvokeLogUtils;

public class LogCommandHandler implements CommandHandler {

	public void handle(String[] args) {
		InvokeLog.init();
		try {
			InvokeLogUtils.buildTraceClass(Helper.findClass(args[0]));
		} catch (Exception e) {
			Agent.print(e.getClass().getName() + ":" + e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "log";
	}
}
