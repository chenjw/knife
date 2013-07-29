package com.chenjw.knife.client.core;

import com.chenjw.knife.core.packet.Packet;

public interface VMConnection {

	public void close() throws Exception;

	public void sendPacket(Packet packet) throws Exception;

	public Packet readPacket() throws Exception;

}
