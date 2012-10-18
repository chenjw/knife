package com.chenjw.knife.client.core;

import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.packet.CommandPacket;

public interface VMConnection {

	public void close() throws Exception;

	public void sendCommand(CommandPacket command) throws Exception;

	public Packet readPacket() throws Exception;

}
