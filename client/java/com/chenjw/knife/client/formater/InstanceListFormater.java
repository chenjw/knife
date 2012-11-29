package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.InstanceListInfo;
import com.chenjw.knife.core.model.ObjectInfo;

public class InstanceListFormater extends BasePrintFormater<InstanceListInfo> {

	@Override
	protected void print(InstanceListInfo instanceListInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level,printer, grep);
		table.setTitle("type", "obj-id", "detail");
		ObjectInfo[] instanceInfos = instanceListInfo.getInstances();
		int i = 0;
		if (instanceInfos != null) {
			for (ObjectInfo info : instanceInfos) {
				table.addLine("[instance]", info.getObjectId(),
						info.getValueString());
				i++;
			}
		}
		table.print();
		this.printLine("find " + i + " instances of " + instanceListInfo.getClassName());
	}

}
