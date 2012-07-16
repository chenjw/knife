package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;

public class GcCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			System.gc();
			Agent.println("gc finished!");

		} catch (Exception e) {
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "gc";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {

	}

}
