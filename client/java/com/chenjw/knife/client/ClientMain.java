package com.chenjw.knife.client;

import java.util.Map.Entry;

import com.chenjw.knife.client.client.CommandClient;
import com.chenjw.knife.client.connector.LocalVMConnector;
import com.chenjw.knife.client.connector.RemoteVMConnector;
import com.chenjw.knife.client.console.JlineCommandConsole;
import com.chenjw.knife.client.console.SimpleCommandConsole;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.CommandService;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.utils.InetHelper;
import com.chenjw.knife.client.utils.ssh.SshClient;
import com.chenjw.knife.utils.JarHelper;
import com.chenjw.knife.utils.PlatformHelper;
import com.chenjw.knife.utils.StringHelper;

public final class ClientMain {

	public static void main(String args[]) throws Exception {
		CommandService console = null;
		// jline对windows的eclipse控制台支持不好
		if (PlatformHelper.isWindows() ){//&& JarHelper.isDevMode()) {
			console = new SimpleCommandConsole();
		} else {
			console = new JlineCommandConsole();
		}
		Client client = new CommandClient(console);
		VMConnector connector = null;
		if (args == null || args.length == 0) {
				connector = new LocalVMConnector();
		} 
		else if(args.length==1 && "view".equals(args[0])){
			if("view".equals(args[0])){
				for(Entry<Object,Object> entry:System.getProperties().entrySet()){
					System.out.println(entry.getKey()+"="+entry.getValue());
				}
			}
		}
		else {
			
			String ip = args[0];
			if (args.length >= 2) {
				String userName = StringHelper.substringBefore(args[1], "/");
				String password = StringHelper.substringAfterLast(args[1], "/");

				String programePath = JarHelper.findJarFolder().getParentFile()
						.getCanonicalPath();
				InetHelper.scp(ip, userName, password, programePath, "/tmp/");
				System.out.println("copy proxy to remote server!");
				final SshClient sshClient = InetHelper.ssh(ip, userName,
						password);
				System.out.println("ssh connected!");

				sshClient.exec("cd /tmp/knife/;sh proxy.sh -d;");
				System.out.println("starting proxy...");
				while (true) {
					String line = sshClient.readLine();
					if (line != null
							&& line.indexOf(Constants.PROXY_STARTED_MESSAGE) != -1) {
						break;
					}
				}
				System.out.println("proxy started!");
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {

						sshClient.close();
					}
				});
			}
			connector = new RemoteVMConnector(ip, Constants.DEFAULT_PROXY_PORT);
		}
		client.start(connector);
	}
}
