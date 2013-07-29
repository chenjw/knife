package com.chenjw.knife.agent.core;

import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

/**
 * 每个CommandHandler实现对应一个方法（比如“find”，“cd”等）的处理逻辑
 * 
 * @author chenjw
 *
 */
public interface CommandHandler {
	public void declareArgs(ArgDef argDef);

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception;

}
