package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.CommandNameListInfo;

public class CommandNameListFormater extends BasePrintFormater<CommandNameListInfo> {

	protected void print(CommandNameListInfo info) {
		this.completeHandler.setCmdCompletors(info.getCommandNames());
	}

}
