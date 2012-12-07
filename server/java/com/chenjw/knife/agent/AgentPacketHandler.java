package com.chenjw.knife.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.args.OptionDesc;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketHandler;
import com.chenjw.knife.core.packet.ObjectPacket;

public class AgentPacketHandler implements PacketHandler, CommandDispatcher {
	private Map<String, CommandHandler> handlerMap = new HashMap<String, CommandHandler>();
	private Map<String, ArgDef> argDefMap = new HashMap<String, ArgDef>();

	public AgentPacketHandler() {
		for (CommandHandler service : ServiceLoader.load(CommandHandler.class,
				AgentPacketHandler.class.getClassLoader())) {
			addCommandHandler(service);
		}

	}

	private void addCommandHandler(CommandHandler commandHandler) {

		ArgDef def = new ArgDef();
		try {
			commandHandler.declareArgs(def);
			argDefMap.put(def.getCommandName(), def);
			Object pre = handlerMap.put(def.getCommandName(), commandHandler);

			if (pre != null) {
				throw new RuntimeException(def.getCommandName()
						+ " has already registered!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void dispatch(Command command) {
		CommandHandler handler = handlerMap.get(command.getName());

		if (handler != null) {
			String argStr = command.getArgs();
			ArgDef def = argDefMap.get(command.getName());
			if ("-h".equals(argStr)) {
				argHelp(def);
				return;
			}
			Args args = new Args();
			try {
				args.parse(argStr, def);

			} catch (Exception e) {
				Agent.sendResult(ResultHelper.newErrorResult("args error, "
						+ e.getClass().getName() + ":"
						+ e.getLocalizedMessage()));
				argHelp(def);
				return;
			}
			try {
				handler.handle(args, this);
			} catch (Exception e) {
				Agent.sendResult(ResultHelper
						.newErrorResult("handle error!", e));
				argHelp(def);
			}

		} else {
			help();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Packet packet) throws Exception {

		ObjectPacket<Command> objectPacket = (ObjectPacket<Command>) packet;
		Command cmd = objectPacket.getObject();
		dispatch(cmd);

	}

	private void help() {
		Agent.info("");
		Agent.info("-------------------------------------------------------");
		Agent.info("  usage: <command> [-h] [<args>]");
		Agent.info("");
		Agent.info("  The most commonly used commands are:");
		Agent.info("");
		int maxN = 30;
		for (Entry<String, ArgDef> entry : argDefMap.entrySet()) {
			Agent.info("   " + entry.getKey()
					+ d(maxN - entry.getKey().length())
					+ entry.getValue().getDesc());
		}
		Agent.info("-------------------------------------------------------");
		Agent.info("");
	}

	private String d(int n) {
		String r = "";
		for (int i = 0; i < n; i++) {
			r += " ";
		}
		return r;
	}

	private void argHelp(ArgDef def) {
		Agent.info("");
		Agent.info("-------------------------------------------------------");
		Agent.info("  usage: " + def.getCommandName() + " " + def.getDef());
		Agent.info("");
		Agent.info("  " + def.getDesc());
		Agent.info("");
		int maxN = 30;
		for (OptionDesc decs : def.getArgDescs()) {
			int length = decs.getFullName().length();
			Agent.info("   " + decs.getFullName() + d(maxN - length)
					+ decs.getDesc());
		}
		for (OptionDesc decs : def.getOptionDescs()) {
			int length = decs.getFullName().length();
			Agent.info("   " + decs.getFullName() + d(maxN - length)
					+ decs.getDesc());
		}
		Agent.info("-------------------------------------------------------");
		Agent.info("");
	}

	public Map<String, ArgDef> getArgDefMap() {
		return argDefMap;
	}

}
