package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.MethodExceptionEndInfo;

public class MethodExceptionEndFormater extends
		BasePrintFormater<MethodExceptionEndInfo> {

	@Override
	protected void print(MethodExceptionEndInfo methodExceptionEndInfo) {
		StringBuffer msg = new StringBuffer(
				d(methodExceptionEndInfo.getDepth()));
		msg.append("[throw] ");

		if (methodExceptionEndInfo.getE() != null) {

			msg.append(methodExceptionEndInfo.getE().getObjectId());
			msg.append(methodExceptionEndInfo.getE().getValueString());
		}
		msg.append(" [" + methodExceptionEndInfo.getTime() + " ms]");
		this.printLine(msg.toString());
	}

	private String d(int dep) {
		String s = "";
		for (int i = 0; i < dep; i++) {
			s += "--";
		}
		return s;
	}
}
