package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.MethodStartInfo;
import com.chenjw.knife.core.model.result.ObjectInfo;

public class MethodStartFormater extends BasePrintFormater<MethodStartInfo> {

	@Override
	protected void print(MethodStartInfo methodFrameInfo) {
		StringBuffer msg = new StringBuffer(d(methodFrameInfo.getDepth()));
		msg.append("[invoke] ");
		if (methodFrameInfo.getThisObjectId() != null) {
			msg.append(methodFrameInfo.getThisObjectId());
		}
		msg.append(methodFrameInfo.getClassName());
		msg.append(".");
		msg.append(methodFrameInfo.getMethodName());
		msg.append("(");
		boolean isFirst = true;
		for (ObjectInfo arg : methodFrameInfo.getArguments()) {
			if (isFirst) {
				isFirst = false;
			} else {
				msg.append(",");
			}
			if (arg == null) {
				msg.append("null");
			} else {
				msg.append(arg.getObjectId());
				msg.append(arg.getValueString());
			}
		}
		msg.append(")");
		if (methodFrameInfo.getLineNum() == -1) {
			msg.append(" <unknow>");
		} else {
			msg.append(" <" + methodFrameInfo.getFileName() + ":");
			msg.append(methodFrameInfo.getLineNum());
			msg.append(">");
		}
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
