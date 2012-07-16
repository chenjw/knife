package com.chenjw.knife.agent;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.agent.handler.CdCommandHandler;
import com.chenjw.knife.agent.handler.ClearCommandHandler;
import com.chenjw.knife.agent.handler.CloseCommandHandler;
import com.chenjw.knife.agent.handler.DoCommandHandler;
import com.chenjw.knife.agent.handler.FindCommandHandler;
import com.chenjw.knife.agent.handler.GcCommandHandler;
import com.chenjw.knife.agent.handler.InvokeCommandHandler;
import com.chenjw.knife.agent.handler.LogCommandHandler;
import com.chenjw.knife.agent.handler.LsCommandHandler;
import com.chenjw.knife.agent.handler.ViewCommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.ObjectPacket;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketHandler;

public class AgentPacketListener implements PacketHandler, CommandDispatcher {
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
		addCommandHandler(new InvokeCommandHandler());
		addCommandHandler(new GcCommandHandler());

	}

	private void addCommandHandler(CommandHandler commandHandler) {
		Object pre = handlerMap.put(commandHandler.getName(), commandHandler);
		if (pre != null) {
			throw new RuntimeException(commandHandler.getName()
					+ " has registered!");
		}
	}

	public void dispatch(Command command) {
		CommandHandler handler = handlerMap.get(command.getName());
		if (handler != null) {
			String args = command.getArgs();
			Map<String, Integer> argDecls = new HashMap<String, Integer>();
			handler.declareArgs(argDecls);
			handler.handle(new Args(args, argDecls), this);
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
