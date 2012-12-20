package com.chenjw.knife.agent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.PacketHandler;

public class AgentServer implements Runnable {
	// 服务器channel对象，负责接受用户的连接
	private ServerSocket serverSocket;
	public PacketHandler handler = null;
	private AgentInfo agentInfo;

	public AgentServer(int port, Instrumentation inst) throws IOException {
		try {
			AgentInfo agentInfo = new AgentInfo();
			agentInfo.setInst(inst);
			this.agentInfo = agentInfo;
			handler = new AgentPacketHandler();
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(port));
			if (port == 0) {
				port = serverSocket.getLocalPort();

			}

		} catch (UnknownHostException e) {

		}
	}

	@Override
	public void run() {

		Socket socket = null;
		try {
			socket = serverSocket.accept();
			InputStream is = socket.getInputStream();
			agentInfo.setSocket(socket);
			Agent.setAgentInfo(agentInfo);
			Agent.info("connected!");

			ServiceRegistry.init();
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
			e.printStackTrace();
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
			ServiceRegistry.clear();
			ServiceRegistry.close();
			Agent.close();
			System.out.println("agent uninstalled!");
		}

	}

}
