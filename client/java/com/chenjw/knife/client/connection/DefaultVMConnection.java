package com.chenjw.knife.client.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.packet.CommandPacket;
import com.chenjw.knife.core.packet.Packet;

/**
 * 远程链接
 * 
 * @author chenjw
 * 
 */
public class DefaultVMConnection implements VMConnection {

	private String ip;
	private int port;

	private Socket socket;
	private InputStream is;
	private OutputStream os;

	private volatile boolean isConnected = false;

	public DefaultVMConnection(String ip, int port) throws IOException {
		this.ip = ip;
		this.port = port;
		connect();
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Command msg = new Command();
				msg.setName("close");
				try {
					if (isConnected) {
						sendPacket(new CommandPacket(msg));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void connect() throws IOException {
		while (!isConnected) {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 3000);
			} catch (Exception e) {
				throw new IOException(ip + ":" + port
						+ " 连接不上，请确保目标机器防火墙端口已打开！", e);
			}
			try {
				is = socket.getInputStream();
				os = socket.getOutputStream();
				addShutdownHook();
				isConnected = true;
				break;
			} catch (Exception e) {
			  e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			   e.printStackTrace();
			}
		}
	}

	public void close() throws IOException {
		if (socket != null && socket.isConnected()) {
			socket.close();
		}
		this.isConnected = false;
	}

	@Override
	public void sendPacket(Packet packet) throws Exception {
		if (!isConnected) {
			throw new Exception("not ready!");
		}
		PacketResolver.write(packet, os);
	}

	@Override
	public Packet readPacket() throws Exception {
		if (!isConnected) {
			throw new Exception("not ready!");
		}
		return PacketResolver.read(is);
	}

}
