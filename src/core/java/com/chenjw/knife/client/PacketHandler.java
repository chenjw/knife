package com.chenjw.knife.client;

public interface PacketHandler {

	public void handle(Packet packet) throws Exception;
}