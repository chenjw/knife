package com.chenjw.knife.server.test.impl;

import com.chenjw.knife.server.test.ApplyModel;
import com.chenjw.knife.server.test.CheckService;

public class CheckServiceImpl implements CheckService {

	public void check(ApplyModel apply) throws Exception {
		if (apply == null) {
			throw new Exception("apply cant be null!");
		}
		if (apply.getId() == null) {
			throw new Exception("id cant be null!");
		}
		if (apply.getMsg() == null) {
			throw new Exception("msg cant be null!");
		}
	}

}
