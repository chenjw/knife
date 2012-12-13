package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.result.Result;

public class ResultPacket extends ObjectPacket<Result> {

	private static final long serialVersionUID = 8087405610667385194L;

	public ResultPacket() {
	}

	public ResultPacket(Result r) {
		super(r);
	}

	@Override
	public String toString() {
		return "[ResultPacket] " + this.getObject();
	}

}
