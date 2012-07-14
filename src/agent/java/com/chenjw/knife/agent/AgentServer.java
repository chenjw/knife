package com.chenjw.knife.agent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.chenjw.knife.client.Packet;
import com.chenjw.knife.client.PacketHandler;
import com.chenjw.knife.client.PacketResolver;

public class AgentServer implements Runnable {
	// 服务器channel对象，负责接受用户的连接
	private static String LOCAL_ADDR = "127.0.0.1";
	private ServerSocket serverSocket;
	public PacketHandler handler = null;
	private Instrumentation inst;

	public AgentServer(int port, Instrumentation inst) throws IOException {
		try {
			this.inst = inst;
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
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			InputStream is = socket.getInputStream();
			Agent.setInfo(new AgentInfo(socket, inst));
			Agent.print("connected!");
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
			System.out.println("agent uninstalled!");
		}

	}

	public static void main(String[] args) throws IOException {
		new Thread(new AgentServer(1111, null)).start();
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
