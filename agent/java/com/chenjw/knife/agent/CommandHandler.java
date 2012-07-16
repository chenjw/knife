package com.chenjw.knife.agent;

import java.util.Map;

import com.chenjw.knife.agent.handler.arg.Args;

public interface CommandHandler {
	public void declareArgs(Map<String, Integer> argDecls);

	public String getName();

	public void handle(Args args, CommandDispatcher dispatcher);

}
