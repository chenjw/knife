package com.chenjw.knife.client;

import com.chenjw.knife.client.client.CommandClient;
import com.chenjw.knife.client.connector.LocalVMConnector;
import com.chenjw.knife.client.connector.RemoteVMConnector;
import com.chenjw.knife.client.console.JlineCommandConsole;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.VMConnector;

public final class ClientMain {

	public static void main(String args[]) throws Exception {
		Client client = new CommandClient(new JlineCommandConsole());
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
