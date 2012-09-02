package com.chenjw.knife.agent.handler;

import java.net.URLDecoder;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.manager.ObjectRecordManager;

public class DecodeCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		String str = args.arg("string-to-decode");
		String decodeStr = URLDecoder.decode(str, "UTF-8");
		Agent.info("[decode] "
				+ ObjectRecordManager.getInstance().toId(decodeStr) + " "
				+ decodeStr);
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("decode");
		argDef.setDef("<string-to-decode>");
		argDef.setDesc("decode string from input code. can be used to input in unsupported encoding.");
		argDef.addOptionDesc("string-to-decode", "the string to be decoded");

	}
}
