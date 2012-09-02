package com.chenjw.knife.agent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.chenjw.knife.agent.manager.Registry;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketHandler;
import com.chenjw.knife.core.PacketResolver;

public class AgentServer implements Runnable {
	// 服务器channel对象，负责接受用户的连接
	private static String LOCAL_ADDR = "127.0.0.1";
	private ServerSocket serverSocket;
	public PacketHandler handler = null;
	private AgentInfo agentInfo;

	public AgentServer(int port, Instrumentation inst,
			ClassLoader baseClassLoader) throws IOException {
		try {
			AgentInfo agentInfo = new AgentInfo();
			agentInfo.setInst(inst);
			agentInfo.setBaseClassLoader(baseClassLoader);
			this.agentInfo = agentInfo;
			handler = new AgentPacketListener();
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName(LOCAL_ADDR));

			if (port == 0) {
				port = serverSocket.getLocalPort();

			}

		} catch (UnknownHostException e) {

		}
	}

	@Override
	public void run() {
		NativeHelper.startClassLoadHook();
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			InputStream is = socket.getInputStream();
			agentInfo.setSocket(socket);
			Agent.setAgentInfo(agentInfo);
			Agent.info("connected!");

			Registry.getInstance().init();
			Packet command = null;
			while (true) {
				command = PacketResolver.read(is);
				try {
					handler.handle(command);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
				}
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
			socket = null;
			Registry.getInstance().clear();
			Registry.getInstance().close();
			Agent.close();
			System.out.println("agent uninstalled!");
		}

	}

}
