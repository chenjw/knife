package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.result.CommandInfo;

public class CommandFormater extends BasePrintFormater<CommandInfo> {

	@Override
	protected void print(CommandInfo info) {
		Command command = new Command();
		command.setName(info.getName());
		command.setArgs(info.getArgs());
		this.commandListener.onCommand(command);
	}
}
