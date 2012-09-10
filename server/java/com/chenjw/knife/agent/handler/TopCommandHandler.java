package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.NativeHelper.ReferenceCount;
import com.chenjw.knife.agent.utils.OSHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.agent.utils.info.ThreadInfo;

public class TopCommandHandler implements CommandHandler {
	private void handleRef(Args args, CommandDispatcher dispatcher) {
		int num = 10;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			num = Integer.parseInt(nOptions.get("num"));
		}
		int i = 0;
		int nameSize = 40;
		int indexSize = 4;
		for (ReferenceCount referenceCount : NativeHelper.countReferree(num)) {
			Agent.info(ToStringHelper.toFixSizeString(String.valueOf(i),
					indexSize)
					+ "[top-ref] "
					+ ToStringHelper.toFixSizeString(ObjectRecordManager
							.getInstance().toId(referenceCount.getObj())
							+ ToStringHelper.toString(referenceCount.getObj()),
							nameSize)
					+ " \t["
					+ referenceCount.getCount()
					+ "]");
			i++;
		}

	}

	private void handleThread(Args args, CommandDispatcher dispatcher) {
		int num = 10;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			num = Integer.parseInt(nOptions.get("num"));
		}
		int i = 0;
		int nameSize = 40;
		int indexSize = 4;
		for (ThreadInfo threadInfo : OSHelper.findTopThread(num)) {
			Agent.info(ToStringHelper.toFixSizeString(String.valueOf(i),
					indexSize)
					+ "[top-thread] "
					+ ToStringHelper.toFixSizeString(threadInfo.getPid() + " "
							+ threadInfo.getName(), nameSize)
					+ " \t["
					+ threadInfo.getCpu() + "%]");
			i++;
		}

	}

	public void handle(Args args, CommandDispatcher dispatcher) {

		String type = args.arg("type");
		if ("ref".equals(type)) {
			handleRef(args, dispatcher);
		} else if ("thread".equals(type)) {
			handleThread(args, dispatcher);
		}
		Agent.info("finished!");

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("top");
		argDef.setDef("[-n <num>] [<type>]");
		argDef.setDesc("find references of target object.");
		argDef.addOptionDesc("type", "ref,thread");
		argDef.addOptionDesc("-n", "top num,default is 10");
	}
}
