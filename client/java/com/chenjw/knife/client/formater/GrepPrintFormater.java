package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;

public abstract class GrepPrintFormater {

	protected String grep;
	protected Level level;
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
		printer.print(level, str);
	}

}
