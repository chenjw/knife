package com.chenjw.knife.client;

import java.io.IOException;

import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketHandler;
import com.chenjw.knife.core.packet.ClosePacket;

public final class ClientMain {
	private static final String OUT_PREFIX = "knife>";
	public static final int DEFAULT_PORT = 2222;

	public static void main(String args[]) {
		int port = DEFAULT_PORT;
		String // pid = JvmHelper.findPid("test_main");
		pid = null;
		try {
			final Client client = new Client(port);
			client.attach(pid);

			client.startHandler(new PacketHandler() {
				@Override
				public void handle(Packet packet) throws IOException {
					if (packet instanceof ClosePacket) {
						System.out.println(OUT_PREFIX + "agent closed!");
						client.close();
					} else {
						System.out.println(OUT_PREFIX + packet);
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

}
