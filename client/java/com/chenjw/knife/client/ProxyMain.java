package com.chenjw.knife.client;

import com.chenjw.knife.client.client.ProxyClient;
import com.chenjw.knife.client.connector.LocalVMConnector;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.VMConnector;

public final class ProxyMain {

	public static void main(String args[]) throws Exception {
		int port = 0;
		if (args == null || args.length == 0) {
			port = Constants.DEFAULT_PROXY_PORT;
		} else {
			port = Integer.parseInt(args[0]);
		}
		Client client = new ProxyClient(port);
		VMConnector connector = new LocalVMConnector();
		client.start(connector);

	}

}
