package com.chenjw.knife.client.formater;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.core.model.result.ClassFieldInfo;
import com.chenjw.knife.core.model.result.FieldInfo;
import com.chenjw.knife.core.model.result.ObjectInfo;

public class ClassFieldFormater extends BasePrintFormater<ClassFieldInfo> {

	@Override
	protected void print(ClassFieldInfo classFieldInfo) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
		table.setTitle("type", "name", "obj-id", "value");
		FieldInfo[] fieldInfos = classFieldInfo.getFields();
		List<String> fieldNames = new ArrayList<String>();
		if (fieldInfos != null) {
			for (FieldInfo field : fieldInfos) {
				fieldNames.add(field.getName());
				ObjectInfo fValue = field.getValue();
				if (fValue == null) {
					table.addLine(field.isStatic() ? "[static-field]"
							: "[field]", field.getName(), "", null);
				} else {
					table.addLine(field.isStatic() ? "[static-field]"
							: "[field]", field.getName(), fValue.getObjectId(),
							fValue.getValueString());
				}

			}
		}
		table.print();
		this.completeHandler.setArgCompletors(fieldNames
				.toArray(new String[fieldNames.size()]));
		this.printLine("finished!");
	}

}
