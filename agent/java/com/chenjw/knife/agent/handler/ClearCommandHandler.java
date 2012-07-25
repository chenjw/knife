package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.log.Profiler;
import com.chenjw.knife.agent.handler.log.InvokeRecord;

public class ClearCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		InvokeRecord.clear();
		Profiler.clear();
	}

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {
	}

}
