package com.chenjw.knife.core.packet;


public interface PacketHandler {

	public void handle(Packet packet) throws Exception;
}