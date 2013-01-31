package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.result.CommandInfo;
import com.chenjw.knife.core.model.result.CommandListInfo;

public class CommandListFormater extends BasePrintFormater<CommandListInfo> {

	@Override
	protected void print(CommandListInfo info) {
		CommandInfo[] infos = info.getCommands();
		if (infos != null) {
			for (CommandInfo commandInfo : infos) {
				Command command = new Command();
				command.setName(commandInfo.getName());
				command.setArgs(commandInfo.getArgs());
				this.commandListener.onCommand(command);
			}
		}
	}
}
