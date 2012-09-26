package com.chenjw.knife.agent.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;

public abstract class AbstractPrintFormater implements PrintFormater {
	private Level level;
	private Printer printer;
	private String grep;

	public AbstractPrintFormater(Level level, Printer printer, String grep) {
		this.level = level;
		this.printer = printer;
		this.grep = grep;
	}

	protected void print(String str) {
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
