package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.PacketResolver;

public class ClosePacket extends ObjectPacket<Object> {
	static final byte CODE = 2;
	static {
		PacketResolver.register(CODE, ClosePacket.class);
	}

	public ClosePacket() {
		super();
	}

	@Override
	public byte getCode() {
		return CODE;
	}

}
