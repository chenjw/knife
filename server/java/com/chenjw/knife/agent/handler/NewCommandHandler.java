package com.chenjw.knife.agent.handler;

import java.lang.reflect.Constructor;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.ParseHelper;
import com.chenjw.knife.utils.StringHelper;

public class NewCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		newInstance(args.arg("expretion"));
	}

	private void newInstance(String constructorSig) throws Exception {
		String argStr = constructorSig;
		String m = StringHelper.substringBefore(argStr, "(");
		m = m.trim();
		Constructor<?> constructor = null;
		if (StringHelper.isNumeric(m)) {
			constructor = ((Constructor<?>[]) ContextManager.getInstance().get(
					Constants.CONSTRUCTOR_LIST))[Integer.parseInt(m)];
		}
		if (constructor == null) {
			Agent.info("cant find constructor!");
			return;
		}
		Object[] mArgs = ParseHelper.parseMethodArgs(
				StringHelper.substringBeforeLast(
						StringHelper.substringAfter(argStr, "("), ")"),
				constructor.getParameterTypes());
		Object obj = newInstance(constructor, mArgs);
		Agent.info(ObjectRecordManager.getInstance().toId(obj) + "created!");
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
		argDef.setCommandName("new");
		argDef.setDef("<expretion>");
		argDef.setDesc("invoke a method of the target object.");
		argDef.addOptionDesc("expretion", "package.ClassName(param1,param2).");
	}
}
