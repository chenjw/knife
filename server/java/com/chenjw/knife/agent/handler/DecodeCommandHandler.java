package com.chenjw.knife.agent.handler;

import java.net.URLDecoder;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.ResultHelper;

public class DecodeCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		String str = args.arg("string-to-decode");
		String decodeStr = URLDecoder.decode(str, "UTF-8");
		Agent.sendResult(ResultHelper.newStringResult("[decode] "
				+ ObjectRecordManager.getInstance().toId(decodeStr) + " "
				+ decodeStr));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("decode");
		argDef.setDef("<string-to-decode>");
		argDef.setDesc("根据输入字符串解码成一个新的字符串，用于输入系统不支持的编码，如中文等。");
		argDef.addOptionDesc("string-to-decode", "待解码的字符串。");

	}
}
