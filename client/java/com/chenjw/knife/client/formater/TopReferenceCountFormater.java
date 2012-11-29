package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.ReferenceCountInfo;
import com.chenjw.knife.core.model.TopReferenceCountInfo;

public class TopReferenceCountFormater extends BasePrintFormater<TopReferenceCountInfo> {



	@Override
	protected void print(TopReferenceCountInfo topReferenceInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level,printer, grep);
		table.setTitle("idx", "obj-id", "info", "ref-count");
		ReferenceCountInfo[] referenceInfos = topReferenceInfo.getReferenceCounts();
		if (referenceInfos != null) {
			int i = 0;
			for (ReferenceCountInfo info : referenceInfos) {
				table.addLine(String.valueOf(i), info.getObj().getObjectId(),
						info.getObj().getValueString(), "[" + info.getCount()
								+ "]");
				i++;
			}
		}
		table.print();
		this.printLine( "finished!");
	}

}
