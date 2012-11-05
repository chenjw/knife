package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.formater.PreparedTableFormater;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.Printer.Level;

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
		Object[] refs = null;
		if (args.option("-r") != null) {
			refs = NativeHelper.findReferreeByObject(obj);
		} else {
			refs = NativeHelper.findReferrerByObject(obj);
		}

		if (refs == null || refs.length == 0) {
			Agent.info("not found! ");
			return;
		}
		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("type", "obj-id", "obj");

		for (Object ref : refs) {
			if (args.option("-r") != null) {
				table.addLine("[referree]", ObjectRecordManager.getInstance()
						.toId(ref), ToStringHelper.toString(ref));
			} else {
				table.addLine("[referrer]", ObjectRecordManager.getInstance()
						.toId(ref), ToStringHelper.toString(ref));
			}
		}
		table.print();
		Agent.info("finished!");

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("ref");
		argDef.setDef("[-r] <object-id>");
		argDef.setDesc("find referrers or referrees of target object.");
		argDef.addOptionDesc("object-id", "a num as the object id.");
		argDef.addOptionDesc("-r", "find referree not Referrer");
	}
}
