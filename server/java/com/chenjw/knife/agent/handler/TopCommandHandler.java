package com.chenjw.knife.agent.handler;

import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.formater.PreparedTableFormater;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.NativeHelper.ReferenceCount;
import com.chenjw.knife.agent.utils.OSHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.agent.utils.info.ThreadInfo;
import com.chenjw.knife.core.Printer.Level;

public class TopCommandHandler implements CommandHandler {
	private void handleRef(Args args, CommandDispatcher dispatcher) {
		int num = 10;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			num = Integer.parseInt(nOptions.get("num"));
		}
		int i = 0;

		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("idx", "obj-id", "info", "ref-count");
		for (ReferenceCount referenceCount : NativeHelper.countReferree(num)) {
			table.addLine(String.valueOf(i), ObjectRecordManager.getInstance()
					.toId(referenceCount.getObj()), ToStringHelper
					.toString(referenceCount.getObj()),
					"[" + referenceCount.getCount() + "]");
			i++;
		}
		table.print();
	}

	private void handleThread(Args args, CommandDispatcher dispatcher) {
		int num = 10;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			num = Integer.parseInt(nOptions.get("num"));
		}
		int i = 0;
		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("idx", "pid", "thread-name", "cpu%");
		for (ThreadInfo threadInfo : OSHelper.findTopThread(num)) {
			table.addLine(String.valueOf(i), threadInfo.getPid(),
					threadInfo.getName(), "[" + threadInfo.getCpu() + "%]");
			i++;
		}
		table.print();

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

	public static void main(String[] args) {
		Integer i1 = 1234;
		Integer i2 = 1234;
		System.out.println(i1 == i2);
	}
}
