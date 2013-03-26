package com.chenjw.knife.agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;

import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.CommandStatusService;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.args.OptionDesc;
import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.packet.ObjectPacket;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.PacketHandler;
import com.chenjw.knife.utils.StringHelper;

public class AgentPacketHandler implements PacketHandler, CommandDispatcher {
	private Map<String, CommandHandler> handlerMap = new HashMap<String, CommandHandler>();
	private Map<String, ArgDef> argDefMap = new HashMap<String, ArgDef>();
	private static String descCnFile = "/command_cn.properties";
	private static String descEnFile = "/command_en.properties";

	public AgentPacketHandler() {
		for (CommandHandler service : ServiceLoader.load(CommandHandler.class,
				AgentPacketHandler.class.getClassLoader())) {
			addCommandHandler(service);
		}
		setDescLanguage("cn");
	}

	public void setDescLanguage(String language) {
		if ("cn".equals(language)) {
			initDescs(descCnFile);
		} else if ("en".equals(language)) {
			initDescs(descEnFile);
		}
	}

	private void initDescs(String file) {
		Properties argDescs = new Properties();
		try {
			argDescs.load(AgentPacketHandler.class.getResourceAsStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Entry<Object, Object> entry : argDescs.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			String cName = StringHelper.substringBefore(key, ".");
			String oName = StringHelper.substringAfter(key, ".");
			if (StringHelper.isEmpty(oName)) {
				argDefMap.get(cName).setDesc(value);
			} else {
				argDefMap.get(cName).addOptionDesc(oName, value);
			}
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
			String argStr = (String) command.getArgs();
			ArgDef def = argDefMap.get(command.getName());
			if ("-h".equals(argStr)) {
				argHelp(def);
				return;
			}
			Args args = new Args();
			try {
				args.parse(argStr, def);

			} catch (Exception e) {
				Agent.sendResult(ResultHelper.newErrorResult(

				"args error, " + e.getClass().getName() + ":"
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

		CommandStatusService commandStatusService = ServiceRegistry
				.getService(CommandStatusService.class);
		// 结束上一次命令
		if (commandStatusService.getCurrentCommand() != null) {
			Agent.sendResult(ResultHelper
					.newErrorResult("The recent command '"
							+ commandStatusService.getCurrentCommand().toString()
							+ "' have not finished! It will be canceled by next command '"+cmd.toString()+"'! "));
		}
		// 设置当前命令
		commandStatusService.setCurrentCommand(cmd);
		try {
			dispatch(cmd);
		} finally {
			if (commandStatusService.getCurrentCommand() != null
					&& !commandStatusService.isWaiting()) {
				Agent.sendResult(ResultHelper.newResult("finished!"));
			}
		}

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
