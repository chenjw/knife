package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.log.Profiler;

public class CloseCommandHandler implements CommandHandler {
	public String getName() {
		return "close";
	}

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		Profiler.clear();
		Agent.close();
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {
	}

}
