package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.ClassInfo;
import com.chenjw.knife.core.model.ClassListInfo;

public class ClassListFormater extends BasePrintFormater<ClassListInfo> {
	

	@Override
	protected void print(ClassListInfo classListInfo) {

		PreparedTableFormater table = new PreparedTableFormater(level,printer, grep);
		table.setTitle("idx", "type", "name", "classloader");
		ClassInfo[] classInfos = classListInfo.getClasses();
		int i=0;
		if (classInfos != null) {
			for (ClassInfo info : classInfos) {
				table.addLine(String.valueOf(i),
						info.isInterface() ? "[interface]" : "[class]",
						info.getName(), "[" + info.getClassLoader() + "]");
				i++;
			}
		}
		table.print();
		this.printLine("find " + i + " classes like '" + classListInfo.getExpretion()
				+ "', please choose one typing like 'find 0'!");
	}

}
