package com.chenjw.knife.core.packet;

import java.io.Serializable;

public interface Packet extends Serializable {

	public byte[] toBytes();

	public void fromBytes(byte[] bytes);
}
