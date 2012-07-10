package com.chenjw.knife.client;

import java.io.IOException;

import com.chenjw.knife.server.Main;
import com.chenjw.knife.utils.JvmUtils;

public final class ClientMain {

	public static void main(String args[]) {
		int port = DEFAULT_PORT;
		String pid = JvmUtils.findPid(Main.PID_ID);
		try {
			final Client client = new Client(port);
			client.attach(pid);

			client.startHandler(new PacketHandler() {
				@Override
				public void handle(Packet packet) throws IOException {
					if (packet instanceof ClosePacket) {
						System.out.println("agent closed!");
						client.close();
					} else {
						System.out.println(packet);
					}
				}
			});
			client.startConsole();
			while (!client.isClosed()) {
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorExit(e.getMessage(), 1);
		}
	}

	private static void errorExit(String msg, int code) {
		System.err.println(msg);
		System.exit(code);
	}

	public static final int DEFAULT_PORT = 2222;

}
