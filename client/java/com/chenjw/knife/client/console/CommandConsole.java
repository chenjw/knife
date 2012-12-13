package com.chenjw.knife.client.console;

import com.chenjw.knife.client.console.divide.FragmentManager;
import com.chenjw.knife.client.core.CommandService;
import com.chenjw.knife.client.core.Completable;
import com.chenjw.knife.client.formater.FormaterManager;
import com.chenjw.knife.client.formater.TypePrintFormater;
import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.result.Result;
import com.chenjw.knife.core.result.divide.Fragment;
import com.chenjw.knife.utils.StringHelper;

public abstract class CommandConsole implements CommandService {
	private Level level;
	private FragmentManager fragmentManager = new FragmentManager();;

	private FormaterManager formaterManager = new FormaterManager(
			new ClientCompletorProcesser());
	private Printer printer = new ClientPrinter();

	public final Command waitCommand() throws Exception {
		while (true) {
			String l;
			l = readConsoleLine();
			if (l != null) {
				Command c = new Command();
				c.setName(StringHelper.substringBefore(l, " "));
				c.setArgs(StringHelper.substringAfter(l, " "));
				return c;
			}
		}
	}

	public final int waitVMIndex() throws Exception {
		int n;
		String line = readConsoleLine();
		try {
			n = Integer.parseInt(line);
		} catch (NumberFormatException e) {
			n = -1;
		}
		return n;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void handleResult(Result r) throws Exception {
		if (r.isSuccess()) {
			Object content = r.getContent();
			if (content != null) {
				Object c = null;
				if (content instanceof Fragment) {
					c = fragmentManager.processFragment((Fragment) content);
				} else {
					c = content;
				}
				if (c != null) {
					TypePrintFormater formater = formaterManager.get(c
							.getClass());
					formater.print(level, printer, c);
				}

			}

		} else {
			writeConsoleLine(r.getErrorMessage());
			if (r.getErrorTrace() != null) {
				for (String line : StringHelper
						.split(r.getErrorMessage(), '\n')) {
					writeConsoleLine(line);
				}
			}
		}
	}

	public final void handleText(String line) throws Exception {
		writeConsoleLine(line);
	}

	public abstract String readConsoleLine() throws Exception;

	public abstract void writeConsoleLine(String line) throws Exception;

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
			try {
				writeConsoleLine(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void debug(String str) {
			try {
				writeConsoleLine(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};
}
