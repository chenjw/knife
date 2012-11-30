package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.ArrayInfo;
import com.chenjw.knife.core.model.ObjectInfo;

public class ArrayFormater extends BasePrintFormater<ArrayInfo> {

	protected void print(ArrayInfo arrayInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level, printer,
				grep);
		table.setTitle("idx", "obj-id", "element");
		ObjectInfo[] arrayElementInfos = arrayInfo.getElements();
		if (arrayElementInfos != null) {
			int i = 0;
			for (ObjectInfo element : arrayElementInfos) {
				table.addLine(String.valueOf(i), element.getObjectId(),
						element.getValueString());
				i++;

			}

		}
		table.print();
		printLine("finished!");
	}

}
