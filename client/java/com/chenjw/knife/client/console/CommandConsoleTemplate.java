package com.chenjw.knife.client.console;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.CommandListenerTemplate;
import com.chenjw.knife.client.core.Completable;
import com.chenjw.knife.client.core.ResponseCallbackTemplate;
import com.chenjw.knife.client.core.ResultModel;
import com.chenjw.knife.client.formater.FormaterManager;
import com.chenjw.knife.client.formater.TypePrintFormater;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;
import com.chenjw.knife.core.model.VMDescriptor;
import com.chenjw.knife.utils.StringHelper;

public abstract class CommandConsoleTemplate extends CommandListenerTemplate {
	private FileWriter fileLog;
	{
		try {
			File file = new File("knife.log");
			fileLog = new FileWriter(file, true);
			System.out.println("log to " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private FormaterManager formaterManager = new FormaterManager(
			new ClientPrinter(), this, new ClientCompletorProcesser());

	public CommandConsoleTemplate() {
		this.callBack = new CommandResponseCallback();
	}

	@SuppressWarnings("unchecked")
	private List<VMDescriptor> sendListVm() {
		Command c = new Command();
		c.setName(Constants.REQUEST_LIST_VM);

		Result r = sendSyncCommand(c);
		// 获得虚拟机列表
		List<VMDescriptor> vmList = (List<VMDescriptor>) r.getContent();
		if (vmList.isEmpty()) {
			handleText("cant find vm process!");
			return null;
		}

		for (int i = 0; i < vmList.size(); i++) {
			handleText(i + ". " + vmList.get(i).getPid() + " "
					+ vmList.get(i).getName());
		}
		String msg = "input [0-" + (vmList.size() - 1) + "] to choose vm! ";
		handleText(msg);
		return vmList;
	}

	private boolean sendRequestAttachVm(List<VMDescriptor> vmList) {

		int n = 0;
		while (true) {
			String msg = "input [0-" + (vmList.size() - 1) + "] to choose vm! ";
			String l;
			l = readConsoleLine();
			if (l != null) {
				try {
					n = Integer.parseInt(l.trim());
				} catch (NumberFormatException e) {
					handleText(msg);
					continue;
				}

			}
			if (n < 0 || n >= vmList.size()) {
				handleText(msg);
				continue;
			}
			break;
		}
		Command c = new Command();
		c.setName(Constants.REQUEST_ATTACH_VM);
		c.setArgs(vmList.get(n).getPid());
		Result result = sendSyncCommand(c);
		return result.isSuccess();
	}

	public void start() {

		Thread th = new Thread(new ConsoleWorker(), "knife-console-worker");
		th.setDaemon(true);
		th.start();

	}

	public final void handleText(String line) {
		writeLogLine(line);
		writeConsoleLine(line);
	}

	private void writeLogLine(String line) {
		try {
			fileLog.write(line + "\n");
			fileLog.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract String readConsoleLine();

	public abstract void writeConsoleLine(String line);

	public abstract void setCompletors(String[]... strs);

	private class ClientCompletorProcesser implements Completable {
		private String[] cmdCompletors;

		@Override
		public void setArgCompletors(String[] strs) {
			setCompletors(cmdCompletors, strs);
		}

		@Override
		public void setCmdCompletors(String[] strs) {
			cmdCompletors = strs;
		}

	}

	private class ClientPrinter extends Printer {

		@Override
		public void info(String str) {
			if (str == null) {
				return;
			}
			try {
				writeConsoleLine(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void debug(String str) {
			if (str == null) {
				return;
			}
			try {
				writeConsoleLine(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	private class CommandResponseCallback extends ResponseCallbackTemplate {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void onSuccess(Object content) {
			if (content != null) {
				if (content != null) {
					TypePrintFormater formater = formaterManager.get(content
							.getClass());
					formater.printObject(content);
				}
			}
		}

		private void onError(String errorMessage, String errorTrace) {
			writeConsoleLine(errorMessage);
			if (errorTrace != null) {
				for (String line : StringHelper.split(errorTrace, '\n')) {
					writeConsoleLine(line);
				}
			}
		}

		@Override
		public void onPart(ResultPart part) {
			try {
				onSuccess(part.getContent());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void done(ResultModel r) {
			try {
				if (r.isSuccess()) {
					onSuccess(r.getContent());
				} else {
					onError(r.getErrorMessage(), r.getErrorTrace());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	private class ConsoleWorker implements Runnable {

		public void run() {
			List<VMDescriptor> vms = sendListVm();
			if (vms == null) {
				return;
			}
			boolean connected = sendRequestAttachVm(vms);
			if (connected) {
				// 获取命令列表
				Command c = new Command();
				c.setName("cmd");
				onCommand(c);
				while (true) {
					String l = null;
					try {
						l = readConsoleLine();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (l != null) {
						Command command = new Command();
						command.setName(StringHelper.substringBefore(l, " "));
						command.setArgs(StringHelper.substringAfter(l, " "));
						onCommand(command);
					}
				}
			}
		}
	}
}
