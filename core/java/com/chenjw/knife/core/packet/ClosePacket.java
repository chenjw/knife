package com.chenjw.knife.core.packet;

import java.io.Serializable;

public class ClosePacket extends ObjectPacket<Serializable> {


	private static final long serialVersionUID = -4317954252973502660L;

	public ClosePacket() {
		super(null);
	}

}
