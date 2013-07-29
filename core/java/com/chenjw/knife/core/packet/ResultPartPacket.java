package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.model.ResultPart;

public class ResultPartPacket extends ObjectPacket<ResultPart> {

	private static final long serialVersionUID = 8087405610667385194L;

	public ResultPartPacket() {
	}

	public ResultPartPacket(ResultPart r) {
		super(r);
	}

	@Override
	public String toString() {
		return "[ResultPartPacket] " + this.getObject();
	}

}
