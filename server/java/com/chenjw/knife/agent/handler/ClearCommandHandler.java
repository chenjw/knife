package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.utils.ResultHelper;

public class ClearCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		ServiceRegistry.clear();
		Agent.sendResult(ResultHelper.newStringResult("clear finished!"));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("clear");
		argDef.setDesc("清除所有字节码增强和agent保存的对象引用，把agent回复到刚连接上时的状态。");
	}

}
