package com.chenjw.knife.core;

public interface Packet {
	public byte getCode();

	public byte[] toBytes();

	public void fromBytes(byte[] bytes);
}
