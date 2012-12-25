package com.chenjw.knife.agent.handler;

import java.io.IOException;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class ViewCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher)
			throws IOException {
		Object thisObject = ServiceRegistry.getService(ContextService.class)
				.get(Constants.THIS);
		if (thisObject == null) {
			Agent.sendResult(ResultHelper.newErrorResult("not found"));
		} else {
			if (args.option("-c") != null) {
				Agent.sendResult(ResultHelper.newErrorResult(thisObject
						.getClass().getClassLoader().getClass().toString()));

			} else if (args.option("-s") != null) {
				Agent.sendResult(ResultHelper.newErrorResult(NativeHelper
						.getClassSourceFileName(thisObject.getClass())));
			} else {
				Agent.sendResult(ResultHelper
						.newErrorResult("option not found"));
			}
		}
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("view [-c] [-s]");
	}

}
