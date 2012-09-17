package com.chenjw.knife.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.args.OptionDesc;
import com.chenjw.knife.agent.handler.CdCommandHandler;
import com.chenjw.knife.agent.handler.ClearCommandHandler;
import com.chenjw.knife.agent.handler.CloseCommandHandler;
import com.chenjw.knife.agent.handler.DecodeCommandHandler;
import com.chenjw.knife.agent.handler.DoCommandHandler;
import com.chenjw.knife.agent.handler.FindCommandHandler;
import com.chenjw.knife.agent.handler.GcCommandHandler;
import com.chenjw.knife.agent.handler.InvokeCommandHandler;
import com.chenjw.knife.agent.handler.LogCommandHandler;
import com.chenjw.knife.agent.handler.LsCommandHandler;
import com.chenjw.knife.agent.handler.NewCommandHandler;
import com.chenjw.knife.agent.handler.PropCommandHandler;
import com.chenjw.knife.agent.handler.RefCommandHandler;
import com.chenjw.knife.agent.handler.SetCommandHandler;
import com.chenjw.knife.agent.handler.SpringCommandHandler;
import com.chenjw.knife.agent.handler.TopCommandHandler;
import com.chenjw.knife.agent.handler.TraceCommandHandler;
import com.chenjw.knife.agent.handler.ViewCommandHandler;
import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketHandler;
import com.chenjw.knife.core.packet.ObjectPacket;

public class AgentPacketListener implements PacketHandler, CommandDispatcher {
	private Map<String, CommandHandler> handlerMap = new HashMap<String, CommandHandler>();
	private Map<String, ArgDef> defMap = new HashMap<String, ArgDef>();

	public AgentPacketListener() {
		addCommandHandler(new CloseCommandHandler());
		addCommandHandler(new ClearCommandHandler());
		addCommandHandler(new LogCommandHandler());
		addCommandHandler(new DoCommandHandler());
		addCommandHandler(new ViewCommandHandler());
		addCommandHandler(new LsCommandHandler());
		addCommandHandler(new SpringCommandHandler());
		addCommandHandler(new FindCommandHandler());
		addCommandHandler(new InvokeCommandHandler());
		addCommandHandler(new GcCommandHandler());
		addCommandHandler(new SetCommandHandler());
		addCommandHandler(new TraceCommandHandler());
		addCommandHandler(new DecodeCommandHandler());
		addCommandHandler(new RefCommandHandler());
		addCommandHandler(new NewCommandHandler());
		addCommandHandler(new PropCommandHandler());
		addCommandHandler(new TopCommandHandler());
		addCommandHandler(new CdCommandHandler());
	}

	private void addCommandHandler(CommandHandler commandHandler) {

		ArgDef def = new ArgDef();
		try {
			commandHandler.declareArgs(def);
			defMap.put(def.getCommandName(), def);
			Object pre = handlerMap.put(def.getCommandName(), commandHandler);

			if (pre != null) {
				throw new RuntimeException(def.getCommandName()
						+ " has registered!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void dispatch(Command command) {
		CommandHandler handler = handlerMap.get(command.getName());

		if (handler != null) {
			String argStr = command.getArgs();
			ArgDef def = defMap.get(command.getName());
			if ("-h".equals(argStr)) {
				argHelp(def);
				return;
			}
			Args args = new Args();
			try {
				args.parse(argStr, def);

			} catch (Exception e) {
				Agent.info("args error, " + e.getClass().getName() + ":"
						+ e.getLocalizedMessage());
				return;
			}
			try {
				handler.handle(args, this);
			} catch (Exception e) {
				e.printStackTrace();
				Agent.info(e.getClass().getName() + ":"
						+ e.getLocalizedMessage());
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
		for (Entry<String, ArgDef> entry : defMap.entrySet()) {
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
}
