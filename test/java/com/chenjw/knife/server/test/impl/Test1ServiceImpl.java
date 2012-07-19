package com.chenjw.knife.server.test.impl;

import com.chenjw.knife.server.test.ApplyModel;
import com.chenjw.knife.server.test.HelloService;
import com.chenjw.knife.server.test.Test1Service;

public class Test1ServiceImpl implements Test1Service {
	private HelloService helloService;

	@Override
	public void doApply(ApplyModel apply) throws Exception {
		if (!"chenjw".equals(apply.getName())) {
			throw new Exception(helloService.hi(apply.getName())
					+ " not allowed");
		}
	}

	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}

}
