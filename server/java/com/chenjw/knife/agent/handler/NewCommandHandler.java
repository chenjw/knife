package com.chenjw.knife.agent.handler;

import java.lang.reflect.Constructor;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.agent.utils.ParseHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.utils.StringHelper;

public class NewCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		newInstance(args.arg("expression"));
	}

	private void newInstance(String constructorSig) throws Exception {
		String argStr = constructorSig;
		String m = StringHelper.substringBefore(argStr, "(");
		m = m.trim();
		Constructor<?> constructor = null;
		if (StringHelper.isNumeric(m)) {
			constructor = ((Constructor<?>[]) ServiceRegistry.getService(
					ContextService.class).get(Constants.CONSTRUCTOR_LIST))[Integer
					.parseInt(m)];
		}
		if (constructor == null) {
			Agent.sendResult(ResultHelper
					.newErrorResult("cant find constructor!"));
			return;
		}
		Object[] mArgs = ParseHelper.parseMethodArgs(
				StringHelper.substringBeforeLast(
						StringHelper.substringAfter(argStr, "("), ")"),
				constructor.getParameterTypes());
		Object obj = newInstance(constructor, mArgs);
		Agent.sendResult(ResultHelper.newResult(ServiceRegistry.getService(
				ObjectHolderService.class).toId(obj)
				+ "created!"));
	}

	private Object newInstance(Constructor<?> constructor, Object[] args)
			throws Exception {
		try {
			constructor.setAccessible(true);
			Object obj = constructor.newInstance(args);
			return obj;
		} finally {
			constructor.setAccessible(false);
		}
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("new <expression>");

	}
}
