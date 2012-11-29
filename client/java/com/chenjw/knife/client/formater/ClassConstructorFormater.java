package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.ClassConstructorInfo;
import com.chenjw.knife.core.model.ConstructorInfo;
import com.chenjw.knife.utils.StringHelper;

public class ClassConstructorFormater extends
		BasePrintFormater<ClassConstructorInfo> {

	@Override
	protected void print(ClassConstructorInfo classConstructorInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level, printer,
				grep);
		table.setTitle("idx", "name");
		ConstructorInfo[] constructorInfos = classConstructorInfo
				.getConstructors();
		if (constructorInfos != null) {
			int i = 0;
			for (ConstructorInfo constructor : constructorInfos) {
				table.addLine(
						String.valueOf(i),
						classConstructorInfo.getClassSimpleName()
								+ "("
								+ StringHelper.join(
										constructor.getParamClassNames(), ",")
								+ ")");
			}
			i++;
		}
		table.print();
		printLine("finished!");
	}

}
