package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.model.ArrayElementInfo;
import com.chenjw.knife.core.model.ArrayInfo;

public class LsArrayFormater extends GrepPrintFormater {

	private ArrayInfo arrayInfo;

	public LsArrayFormater(Printer printer, String grep) {
		super(printer, grep);
	}

	@Override
	public void print(Level level) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
		table.setTitle("idx", "obj-id", "element");
		ArrayElementInfo[] arrayElementInfos = arrayInfo.getArrayElements();
		if (arrayElementInfos != null) {
			int i = 0;
			for (ArrayElementInfo element : arrayElementInfos) {

				table.addLine(String.valueOf(i), element.getObjectId(),
						element.getValueString());

			}
			i++;
		}
		table.print(level);
		this.print(level, "finished!");
	}

}
