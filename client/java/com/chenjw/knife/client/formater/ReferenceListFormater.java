package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.ObjectInfo;
import com.chenjw.knife.core.model.result.ReferenceListInfo;

public class ReferenceListFormater extends BasePrintFormater<ReferenceListInfo> {

	@Override
	protected void print(ReferenceListInfo referenceListInfo) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
		table.setTitle("type", "obj-id", "obj");
		ObjectInfo[] references = referenceListInfo.getReferences();
		if (references != null) {
			for (ObjectInfo info : references) {
				table.addLine(referenceListInfo.isReferree() ? "[referree]"
						: "[referrer]", info.getObjectId(), info
						.getValueString());
			}
		}
		table.print();
		this.printLine("finished!");
	}

}
