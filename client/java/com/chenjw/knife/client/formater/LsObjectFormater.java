package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.model.ObjectInfo;
import com.chenjw.knife.utils.StringHelper;

public class LsObjectFormater extends GrepPrintFormater {

	private ObjectInfo objectInfo;

	public LsObjectFormater(Printer printer, String grep) {
		super(printer, grep);
	}

	@Override
	public void print(Level level) {
		if (objectInfo.isThrowable()) {
			this.print(level, " " + objectInfo.getObjectId());
			for (String line : StringHelper.split(objectInfo.getValueString(),
					'\n')) {
				this.print(level, line);
			}
		} else {
			this.print(
					level,
					" " + objectInfo.getObjectId()
							+ objectInfo.getValueString());
		}
		this.print(level, "finished!");
	}
}
