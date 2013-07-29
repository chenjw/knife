package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.ExceptionInfo;
import com.chenjw.knife.utils.StringHelper;

public class ExceptionFormater extends BasePrintFormater<ExceptionInfo> {



	@Override
	protected void print(ExceptionInfo exceptionInfo) {
		this.printLine( " " + exceptionInfo.getObjectId());
		for (String line : StringHelper.split(exceptionInfo.getTraceString(),
				'\n')) {
			this.printLine( line);
		}
		this.printLine( "finished!");
	}
}
