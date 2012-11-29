package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.model.ClassFieldInfo;
import com.chenjw.knife.core.model.FieldInfo;

public class LsFieldFormater extends GrepPrintFormater {

	private ClassFieldInfo classFieldInfo;

	public LsFieldFormater(Printer printer, String grep) {
		super(printer, grep);
	}

	@Override
	public void print(Level level) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
		table.setTitle("type", "name", "obj-id", "value");
		FieldInfo[] fieldInfos = classFieldInfo.getFields();
		if (fieldInfos != null) {
			for (FieldInfo field : fieldInfos) {
				table.addLine(field.isStatic() ? "[static-field]" : "[field]",
						field.getName(), field.getObjectId(),
						field.getValueString());
			}
		}
		table.print(level);
		this.print(level, "finished!");
	}

}
