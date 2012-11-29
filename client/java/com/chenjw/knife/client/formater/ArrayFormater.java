package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.ArrayElementInfo;
import com.chenjw.knife.core.model.ArrayInfo;

public class ArrayFormater extends BasePrintFormater<ArrayInfo> {

	protected void print(ArrayInfo arrayInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level,printer, grep);
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
		table.print();
		printLine("finished!");
	}


}
