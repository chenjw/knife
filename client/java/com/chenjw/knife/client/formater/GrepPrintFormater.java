package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;

public abstract class GrepPrintFormater {

	protected String grep;
	protected Printer printer;

	protected final void printLine(String str) {
		if (printer == null) {
			return;
		}
		if (str == null) {
			return;
		}
		if (grep != null) {
			if (str.indexOf(grep) == -1) {
				return;
			}
		}
		printer.info(str);
	}

	public void setPrinter(Printer printer) {
		this.printer = printer;
	}

}
