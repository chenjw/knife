package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.MethodStatInfo;
import com.chenjw.knife.core.model.result.MethodStatListInfo;

public class MethodStatListFormater extends BasePrintFormater<MethodStatListInfo> {

	@Override
	protected void print(MethodStatListInfo methodStatListInfo) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
		table.setTitle("idx","methed", "count");
		MethodStatInfo[] methodStatInfos = methodStatListInfo.getMethodStatInfos();
		if (methodStatInfos != null) {
			int i = 0;
			for (MethodStatInfo info : methodStatInfos) {
				table.addLine(String.valueOf(i), info.getMethod(), String.valueOf(info.getCount()));
				i++;
			}
		}
		table.print();
		this.printLine("finished!");
	}

}
