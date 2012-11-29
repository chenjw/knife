package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.model.ObjectInfo;

public class CdObjectFormater extends GrepPrintFormater {

	private ObjectInfo objectInfo;

	public CdObjectFormater(Printer printer, String grep) {
		super(printer, grep);
	}

	@Override
	public void print(Level level) {
		this.print(
				level,
				"into " + objectInfo.getObjectId()
						+ objectInfo.getValueString());
	}
}
