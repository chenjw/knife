package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.CommandListInfo;

public class CommandListFormater extends BasePrintFormater<CommandListInfo> {

	protected void print(CommandListInfo info) {
		this.completeHandler.setCmdCompletors(info.getCommandNames());
	}

}
