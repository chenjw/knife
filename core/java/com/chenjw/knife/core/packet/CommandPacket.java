package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.model.Command;

public class CommandPacket extends ObjectPacket<Command> {

	private static final long serialVersionUID = -2342736927367363516L;

	public CommandPacket(Command request) {
		super(request);
	}

	public CommandPacket() {
	}

	@Override
	public String toString() {
		return "[CommandPacket] " + this.getObject();
	}

}
