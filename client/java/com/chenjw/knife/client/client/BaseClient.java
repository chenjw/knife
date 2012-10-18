package com.chenjw.knife.client.client;

import java.util.List;

import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.model.VMDescriptor;
import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.packet.ClosePacket;
import com.chenjw.knife.core.packet.CommandPacket;
import com.chenjw.knife.utils.StringHelper;

public abstract class BaseClient implements Client {

	private volatile boolean isRunning = false;

	public void start(VMConnector connector) throws Exception {

		// 获得虚拟机列表
		List<VMDescriptor> vmList = connector.listVM();
		if (vmList.isEmpty()) {
			writeLine("cant find vm process!");
			return;
		}
		for (int i = 0; i < vmList.size(); i++) {
			writeLine(i + ". " + vmList.get(i).getName());
		}
		String msg = "input [0-" + (vmList.size() - 1) + "] to choose vm! ";
		writeLine(msg);
		int n = 0;
		while (true) {
			String line = readLine();
			try {
				n = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				writeLine(msg);
				continue;
			}
			if (n < 0 || n >= vmList.size()) {
				writeLine(msg);
				continue;
			}
			break;
		}
		connector.attachVM(vmList.get(n).getId(), Constants.DEFAULT_AGENT_PORT);
		VMConnection conn = connector
				.createVMConnection(Constants.DEFAULT_AGENT_PORT);
		isRunning = true;
		startPacketReader(conn);
		startPacketSeader(conn);
		while (isRunning) {
			Thread.sleep(3000);
		}
	}

	private void startPacketReader(VMConnection conn) {
		Thread t = new Thread(new PacketReader(conn), "knife-packet-reader");
		t.setDaemon(true);
		t.start();
	}

	private void startPacketSeader(VMConnection conn) {
		Thread t = new Thread(new PacketSender(conn), "knife-packet-sender");
		t.setDaemon(true);
		t.start();
	}

	public abstract String readLine() throws Exception;

	public abstract void writeLine(String line) throws Exception;

	public abstract void close() throws Exception;

	private class PacketSender implements Runnable {
		private VMConnection conn;

		public PacketSender(VMConnection conn) {
			this.conn = conn;
		}

		@Override
		public void run() {
			try {
				while (isRunning) {
					String l;
					l = readLine();
					if (l != null) {
						Command c = new Command();
						c.setName(StringHelper.substringBefore(l, " "));
						c.setArgs(StringHelper.substringAfter(l, " "));
						conn.sendCommand(new CommandPacket(c));
					}
				}
			} catch (Exception e) {
			}
		}
	}

	private class PacketReader implements Runnable {
		private VMConnection conn;

		public PacketReader(VMConnection conn) {
			this.conn = conn;
		}

		@Override
		public void run() {
			try {
				while (isRunning) {
					Packet p = conn.readPacket();
					if (p instanceof ClosePacket) {
						conn.close();
						close();
						isRunning = false;
					} else {
						writeLine(p.toString());
					}

				}
			} catch (Exception e) {
			}
		}
	}

}
