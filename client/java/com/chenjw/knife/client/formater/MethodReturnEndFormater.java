package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.MethodReturnEndInfo;

public class MethodReturnEndFormater extends
		BasePrintFormater<MethodReturnEndInfo> {

	@Override
	protected void print(MethodReturnEndInfo methodReturnEndInfo) {
		StringBuffer msg = new StringBuffer(d(methodReturnEndInfo.getDepth()));
		msg.append("[return] ");
		if (methodReturnEndInfo.isVoid()) {
			msg.append("void");
		} else if (methodReturnEndInfo.getResult() == null) {
			msg.append("null");
		} else {
			msg.append(methodReturnEndInfo.getResult().getObjectId());
			msg.append(methodReturnEndInfo.getResult().getValueString());
		}
		msg.append(" [" + methodReturnEndInfo.getTime() + " ms]");

		this.printLine( msg.toString());
	}

	private String d(int dep) {
		String s = "";
		for (int i = 0; i < dep; i++) {
			s += "--";
		}
		return s;
	}
}
