package com.chenjw.knife.test.impl;

import com.chenjw.knife.test.ApplyModel;
import com.chenjw.knife.test.Test1Service;

public class Test1ServiceImpl implements Test1Service {

	@Override
	public void doApply(ApplyModel apply) throws Exception {
		if (!"chenjw".equals(apply.getName())) {
			throw new Exception("not allowed");
		}
	}
}
