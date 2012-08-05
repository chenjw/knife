package com.chenjw.knife.agent.handler;

import java.lang.reflect.Constructor;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.service.ContextManager;
import com.chenjw.knife.agent.service.ObjectRecordManager;
import com.chenjw.knife.agent.util.ParseHelper;
import com.chenjw.knife.agent.util.StringHelper;

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
			Agent.println("cant find constructor!");
			return;
		}
		Object[] mArgs = ParseHelper.parseMethodArgs(
				StringHelper.substringBeforeLast(
						StringHelper.substringAfter(argStr, "("), ")"),
				constructor.getParameterTypes());
		Object obj = newInstance(constructor, mArgs);
		Agent.println(ObjectRecordManager.getInstance().toId(obj) + "created!");
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
