package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.result.Result;

public class ResultPacket extends ObjectPacket<Result<?>> {
	public ResultPacket() {
	}

	public ResultPacket(Result<?> r) {
		super(r);
	}

	@Override
	public String toString() {
		return "[ResultPacket] " + this.getObject();
	}

}
