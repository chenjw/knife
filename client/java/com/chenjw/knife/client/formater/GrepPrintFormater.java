package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;

public abstract class GrepPrintFormater implements PrintFormater{

	protected Printer printer;
	protected String grep;

	public GrepPrintFormater(Printer printer, String grep) {

		this.printer = printer;
		this.grep = grep;
	}

	protected void print(Level level, String str) {
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
		printer.print(level, str);
	}

}
