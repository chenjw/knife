package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.ThreadInfo;
import com.chenjw.knife.core.model.result.TopThreadInfo;

public class TopThreadFormater extends BasePrintFormater<TopThreadInfo> {

	@Override
	protected void print(TopThreadInfo topThreadInfo) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
		table.setTitle("idx", "tid", "thread-name", "cpu%");
		ThreadInfo[] threadInfos = topThreadInfo.getThreads();
		if (threadInfos != null) {
			int i = 0;
			for (ThreadInfo info : threadInfos) {
				table.addLine(String.valueOf(i), info.getTid(), info.getName(),
						"[" + info.getCpu() + "%]");
				i++;
			}
		}
		table.print();
		this.printLine("finished!");
	}

}
