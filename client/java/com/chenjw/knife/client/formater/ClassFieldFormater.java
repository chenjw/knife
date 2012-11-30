package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.ClassFieldInfo;
import com.chenjw.knife.core.model.FieldInfo;
import com.chenjw.knife.core.model.ObjectInfo;

public class ClassFieldFormater extends BasePrintFormater<ClassFieldInfo> {

	@Override
	protected void print(ClassFieldInfo classFieldInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level, printer,
				grep);
		table.setTitle("type", "name", "obj-id", "value");
		FieldInfo[] fieldInfos = classFieldInfo.getFields();
		if (fieldInfos != null) {
			for (FieldInfo field : fieldInfos) {
				ObjectInfo fValue = field.getValue();
				if (fValue == null) {
					table.addLine(field.isStatic() ? "[static-field]"
							: "[field]", "", null);
				} else {
					table.addLine(field.isStatic() ? "[static-field]"
							: "[field]", field.getName(), fValue.getObjectId(),
							fValue.getValueString());
				}

			}
		}
		table.print();
		this.printLine("finished!");
	}

}
