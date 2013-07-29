package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.ObjectInfo;

public class ObjectFormater extends BasePrintFormater<ObjectInfo> {

	@Override
	protected void print(ObjectInfo objectInfo) {

		this.printLine(
				" " + objectInfo.getObjectId() + objectInfo.getValueString());

		this.printLine( "finished!");
	}
}
