package com.chenjw.knife.client;

public interface Packet {
	public byte getCode();

	public byte[] toBytes();

	public void fromBytes(byte[] bytes);
}
