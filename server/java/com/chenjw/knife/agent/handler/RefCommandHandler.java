package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;

public class RefCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if (param == null) {
			obj = ContextManager.getInstance().get(Constants.THIS);
		} else {
			int id = Integer.parseInt(param);
			obj = ObjectRecordManager.getInstance().get(id);
		}

		if (obj == null) {
			Agent.info("id not found! ");
			return;
		}
		Object[] refs = NativeHelper.findReferrerByObject(obj);
		if (refs == null || refs.length == 0) {
			Agent.info("not found! ");
			return;
		}
		for (Object ref : refs) {
			Agent.info("[ref] "
					+ ObjectRecordManager.getInstance().toId(ref)
					+ ToStringHelper.toString(ref));
		}
		Agent.info("finished!");

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("ref");
		argDef.setDef("<object-id>");
		argDef.setDesc("find references of target object.");
		argDef.addOptionDesc("object-id", "a num as the object id.");
	}
}
