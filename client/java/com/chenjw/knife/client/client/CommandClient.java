package com.chenjw.knife.client.client;

import java.util.List;

import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.CommandService;
import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;
import com.chenjw.knife.core.model.VMDescriptor;
import com.chenjw.knife.core.packet.ClosePacket;
import com.chenjw.knife.core.packet.CommandPacket;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.ResultPacket;
import com.chenjw.knife.core.packet.ResultPartPacket;

public class CommandClient implements Client {

	private volatile boolean isRunning = false;

	private CommandService commandService;

	public CommandClient(CommandService handler) {
		this.commandService = handler;
	}

	public void start(VMConnector connector) throws Exception {
		VMConnection conn = null;
		// 等待启动
		while (true) {
			Command command = commandService.waitCommand();
			if (Constants.REQUEST_LIST_VM.equals(command.getName())) {
				List<VMDescriptor> r = connector.listVM();
				Result result = new Result(command.getId());
				result.setSuccess(true);
				result.setContent(r);

				commandService.handleResult(result);
			} else if (Constants.REQUEST_ATTACH_VM.equals(command.getName())) {
				connector.attachVM(command.getArgs().toString(),
						Constants.DEFAULT_AGENT_PORT);
				conn = connector
						.createVMConnection(Constants.DEFAULT_AGENT_PORT);
				Result result = new Result(command.getId());
				result.setSuccess(true);
				commandService.handleResult(result);
				break;
			}
		}

		isRunning = true;

		startPacketReader(conn);
		startPacketSender(conn);
		while (isRunning) {
			Thread.sleep(3000);
		}
	}

	private void startPacketReader(VMConnection conn) {
		Thread t = new Thread(new PacketReader(conn), "knife-packet-reader");
		t.setDaemon(true);
		t.start();
	}

	private void startPacketSender(VMConnection conn) {
		Thread t = new Thread(new PacketSender(conn), "knife-packet-sender");
		t.setDaemon(true);
		t.start();
	}

	private class PacketSender implements Runnable {
		private VMConnection conn;

		public PacketSender(VMConnection conn) {
			this.conn = conn;
		}

		@Override
		public void run() {

			while (isRunning) {
				try {
					Command c = commandService.waitCommand();
					conn.sendPacket(new CommandPacket(c));
				} catch (Exception e) {
					e.printStackTrace();
				}
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
						commandService.close();
						isRunning = false;
					} else if (p instanceof ResultPacket) {
						Result r = ((ResultPacket) p).getObject();
						if (r != null) {
							commandService.handleResult(r);
						}

					} else if (p instanceof ResultPartPacket) {
						ResultPart r = ((ResultPartPacket) p).getObject();
						if (r != null) {
							commandService.handlePart(r);
						}

					}

					else {
						commandService.handleText(p.toString());
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
