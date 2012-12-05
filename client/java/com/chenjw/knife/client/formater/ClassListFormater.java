package com.chenjw.knife.client.formater;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.core.model.ClassInfo;
import com.chenjw.knife.core.model.ClassListInfo;

public class ClassListFormater extends BasePrintFormater<ClassListInfo> {
	

	@Override
	protected void print(ClassListInfo classListInfo) {

		PreparedTableFormater table = new PreparedTableFormater(level,printer, grep);
		table.setTitle("idx", "type", "name", "classloader");
		ClassInfo[] classInfos = classListInfo.getClasses();
		int i=0;
		List<String> classNames=new ArrayList<String>();
		if (classInfos != null) {
			for (ClassInfo info : classInfos) {
				classNames.add(info.getName());
				table.addLine(String.valueOf(i),
						info.isInterface() ? "[interface]" : "[class]",
						info.getName(), "[" + info.getClassLoader() + "]");
				i++;
			}
		}
		table.print();
		this.completeHandler.setArgCompletors(classNames.toArray(new String[classNames.size()]));
		this.printLine("find " + i + " classes like '" + classListInfo.getExpression()
				+ "', please choose one typing like 'find 0'!");
	}

}
