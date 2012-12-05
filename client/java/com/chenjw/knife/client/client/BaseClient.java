package com.chenjw.knife.client.client;

import java.util.List;

import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.Completable;
import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.formater.FormaterManager;
import com.chenjw.knife.client.formater.TypePrintFormater;
import com.chenjw.knife.client.model.VMDescriptor;
import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.packet.ClosePacket;
import com.chenjw.knife.core.packet.CommandPacket;
import com.chenjw.knife.core.packet.ResultPacket;
import com.chenjw.knife.core.result.Result;
import com.chenjw.knife.utils.StringHelper;

public abstract class BaseClient implements Client {
	private FormaterManager formaterManager;
	private volatile boolean isRunning = false;
	private Level level;

	private Printer printer = new Printer() {

		@Override
		public void info(String str) {
			try {
				writeLine(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void debug(String str) {
			try {
				writeLine(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};
	private String[] cmdCompletors;
	{
		formaterManager = new FormaterManager(new Completable() {
			@Override
			public void setArgCompletors(String[] strs) {
				BaseClient.this.setCompletors(cmdCompletors, strs);
			}

			@Override
			public void setCmdCompletors(String[] strs) {
				cmdCompletors = strs;
			}

		});
	}

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
		// 获取命令列表
		Command c = new Command();
		c.setName("cmd");
		conn.sendCommand(new CommandPacket(c));
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

	public abstract void setCompletors(String[]... strs);

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

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void run() {
			try {
				while (isRunning) {
					Packet p = conn.readPacket();
					if (p instanceof ClosePacket) {
						conn.close();
						close();
						isRunning = false;
					} else if (p instanceof ResultPacket) {
						Result<?> r = ((ResultPacket) p).getObject();
						if (r.isSuccess()) {
							Object content = r.getContent();
							if (content != null) {
								TypePrintFormater formater = formaterManager
										.get(content.getClass());
								formater.print(level, printer, content);
							}

						} else {
							writeLine(r.getErrorMessage());
							if (r.getErrorTrace() != null) {
								for (String line : StringHelper.split(
										r.getErrorMessage(), '\n')) {
									writeLine(line);
								}
							}
						}
					} else {
						writeLine(p.toString());
					}

				}
			} catch (Exception e) {
			}
		}
	}

}
