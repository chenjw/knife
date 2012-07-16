package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.log.InvokeLogUtils;

public class LogCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		try {
			InvokeLogUtils.buildTraceClass(0, Helper.findClass(args.arg(0)));
		} catch (Exception e) {
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "log";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {

	}
}
