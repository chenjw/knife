package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.core.model.ClassConstructorInfo;
import com.chenjw.knife.core.model.ConstructorInfo;
import com.chenjw.knife.utils.StringHelper;

public class LsConstructorInfoFormater extends GrepPrintFormater {

	private ClassConstructorInfo classConstructorInfo;

	public LsConstructorInfoFormater(Printer printer, String grep) {
		super(printer, grep);
	}

	@Override
	public void print(Level level) {
		PreparedTableFormater table = new PreparedTableFormater(printer, grep);
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
		table.print(level);
		this.print(level, "finished!");
	}

}
