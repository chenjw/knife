package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.Command;

public class CommandPacket extends ObjectPacket<Command> {

	public CommandPacket(Command command) {
		super(command);
	}

	public CommandPacket() {
	}

	@Override
	public String toString() {
		return "[CommandPacket] " + this.getObject();
	}

}
