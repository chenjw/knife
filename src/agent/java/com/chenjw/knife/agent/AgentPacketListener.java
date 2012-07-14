package com.chenjw.knife.agent;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.agent.handler.CdCommandHandler;
import com.chenjw.knife.agent.handler.ClearCommandHandler;
import com.chenjw.knife.agent.handler.CloseCommandHandler;
import com.chenjw.knife.agent.handler.DoCommandHandler;
import com.chenjw.knife.agent.handler.FindCommandHandler;
import com.chenjw.knife.agent.handler.LogCommandHandler;
import com.chenjw.knife.agent.handler.LsCommandHandler;
import com.chenjw.knife.agent.handler.ViewCommandHandler;
import com.chenjw.knife.client.Command;
import com.chenjw.knife.client.ObjectPacket;
import com.chenjw.knife.client.Packet;
import com.chenjw.knife.client.PacketHandler;

public class AgentPacketListener implements PacketHandler {
	private Map<String, CommandHandler> handlerMap = new HashMap<String, CommandHandler>();

	public AgentPacketListener() {
		addCommandHandler(new CloseCommandHandler());
		addCommandHandler(new ClearCommandHandler());
		addCommandHandler(new LogCommandHandler());
		addCommandHandler(new DoCommandHandler());
		addCommandHandler(new ViewCommandHandler());
		addCommandHandler(new LsCommandHandler());
		addCommandHandler(new CdCommandHandler());
		addCommandHandler(new FindCommandHandler());
	}

	private void addCommandHandler(CommandHandler commandHandler) {
		handlerMap.put(commandHandler.getName(), commandHandler);
	}

	private void dispatch(Command command) {
		CommandHandler handler = handlerMap.get(command.getName());
		if (handler != null) {
			handler.handle(command.getArgs());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Packet packet) throws Exception {
		ObjectPacket<Command> objectPacket = (ObjectPacket<Command>) packet;
		Command cmd = objectPacket.getObject();
		dispatch(cmd);

	}
}
