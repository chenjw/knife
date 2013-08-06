package com.chenjw.knife.client;

import java.util.Map;

import com.chenjw.knife.client.client.ProxyClient;
import com.chenjw.knife.client.connector.LocalVMConnector;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.utils.JarHelper;
import com.chenjw.knife.utils.StringHelper;

public final class ProxyMain {
	private static final ArgDef ARG_DEF = new ArgDef();
	private static final String commandName = "proxymain";
	static {
		ARG_DEF.setDefinition(commandName + " [-p <port>] [-d]");
	}

	public static void main(String[] args) throws Exception {

		String argStr = StringHelper.join(args, " ");
		Args arg = new Args();

		arg.parse(argStr, ARG_DEF);

		int port = Constants.DEFAULT_PROXY_PORT;
		Map<String, String> pOptions = arg.option("-p");
		if (pOptions != null) {
			port = Integer.parseInt(pOptions.get("port"));
		}
		Map<String, String> dOptions = arg.option("-d");
		if (dOptions != null) {
			JarHelper.removeKnifeDirOnExit();
		}
		Client client = new ProxyClient(port);
		VMConnector connector = new LocalVMConnector();
		client.start(connector);

	}
}
