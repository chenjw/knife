package com.chenjw.knife.client;

import com.chenjw.knife.client.client.CommandClient;
import com.chenjw.knife.client.connector.LocalVMConnector;
import com.chenjw.knife.client.connector.RemoteVMConnector;
import com.chenjw.knife.client.console.JlineCommandConsole;
import com.chenjw.knife.client.console.SimpleCommandConsole;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.CommandService;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.utils.JarHelper;
import com.chenjw.knife.utils.PlatformHelper;

public final class ClientMain {

	public static void main(String args[]) throws Exception {
		CommandService console = null;
		// jline对windows的eclipse控制台支持不好
		if (PlatformHelper.isWindows() && JarHelper.isDevMode()) {
			console = new SimpleCommandConsole();
		} else {
			console = new JlineCommandConsole();
		}
		Client client = new CommandClient(console);
		VMConnector connector = null;
		if (args == null || args.length == 0) {
			connector = new LocalVMConnector();
		} else {
			connector = new RemoteVMConnector(args[0],
					Constants.DEFAULT_PROXY_PORT);
		}
		client.start(connector);
	}
}
