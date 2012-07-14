package com.chenjw.knife.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.ObjectPacket;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketHandler;
import com.chenjw.knife.core.PacketResolver;
import com.sun.tools.attach.VirtualMachine;

public class Client {

	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private VirtualMachine vm = null;

	private volatile boolean isClosed = true;

	public Client(int port) {
		this.port = port;
	}

	public void attach(String pid) throws IOException {
		try {
			String agentPath = "/home/chenjw/my_workspace/knife/dist/knife/lib/knife-agent.jar";

			attach(pid, agentPath, null, null);
			connect();
		} catch (RuntimeException re) {
			throw re;
		} catch (IOException ioexp) {
			throw ioexp;
		} catch (Exception exp) {
			throw new IOException(exp.getMessage());
		}
	}

	public void attach(String pid, String agentPath, String sysCp, String bootCp)
			throws IOException {
		try {
			vm = VirtualMachine.attach(pid);
			String agentArgs = (new StringBuilder()).append("port=")
					.append(port).toString();
			if (bootCp != null)
				agentArgs = (new StringBuilder()).append(agentArgs)
						.append("&bootClassPath=").append(bootCp).toString();
			if (sysCp == null)
				sysCp = getToolsJarPath();
			agentArgs = (new StringBuilder()).append(agentArgs)
					.append("&systemClassPath=").append(sysCp).toString();
			vm.loadAgentPath(
					"/home/chenjw/workspace/jvmtitest/src/.libs/libagent.so",
					agentArgs);

			vm.loadAgent(agentPath, agentArgs);

			// vm.detach();
		} catch (RuntimeException re) {
			throw re;
		} catch (IOException ioexp) {
			throw ioexp;
		} catch (Exception exp) {
			throw new IOException(exp.getMessage());
		}
	}

	private void connect() throws IOException {
		while (isClosed) {
			try {
				socket = new Socket("127.0.0.1", port);
				is = socket.getInputStream();
				os = socket.getOutputStream();
				isClosed = false;
				break;
			} catch (Exception e) {
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Command msg = new Command();
				msg.setName("close");
				try {
					if (!isClosed) {
						send(new ObjectPacket<Command>(msg));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void close() throws IOException {
		if (socket != null && socket.isConnected()) {
			socket.close();
		}
		this.isClosed = true;
	}

	public boolean isClosed() {
		return this.isClosed;
	}

	public void send(Packet command) throws IOException {
		PacketResolver.write(command, os);
	}

	private Packet read() throws IOException {
		return PacketResolver.read(is);
	}

	public void startConsole() {
		Thread t = new Thread("client-console") {
			@Override
			public void run() {
				try {
					Scanner stdin = new Scanner(System.in);
					String line = null;
					while (!isClosed()) {
						line = stdin.nextLine();
						String[] l = line.split(" ");
						if (l.length == 0) {
							continue;
						}
						String name = l[0];
						String[] arguments = Arrays.copyOfRange(l, 1, l.length);
						Command command = new Command();
						command.setName(name);
						command.setArgs(arguments);
						send(new ObjectPacket<Command>(command));
					}
				} catch (IOException e) {
				}
			}
		};
		t.setDaemon(true);
		t.start();

	}

	public void startHandler(final PacketHandler packetHandler) {
		Thread t = new Thread("client-handler") {
			@Override
			public void run() {
				try {
					while (!isClosed()) {
						Packet cmd = read();
						if (packetHandler != null) {
							packetHandler.handle(cmd);
						}
					}
				} catch (Exception e) {
					return;
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	private String getToolsJarPath() {
		String components[] = System.getProperty("java.class.path").split(
				File.pathSeparator);
		String arr$[] = components;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			String c = arr$[i$];
			if (c.endsWith("tools.jar"))
				return (new File(c)).getAbsolutePath();
			if (c.endsWith("classes.jar"))
				return (new File(c)).getAbsolutePath();
		}

		if (System.getProperty("os.name").startsWith("Mac")) {
			String java_home = System.getProperty("java.home");
			String java_mac_home = java_home.substring(0,
					java_home.indexOf("/Home"));
			return (new StringBuilder()).append(java_mac_home)
					.append("/Classes/classes.jar").toString();
		} else {
			return (new StringBuilder())
					.append(System.getProperty("java.home"))
					.append("../lib/tools.jar").toString();
		}
	}

	private int port;

}
