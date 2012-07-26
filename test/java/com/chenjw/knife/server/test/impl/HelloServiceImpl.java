package com.chenjw.knife.server.test.impl;

import com.chenjw.knife.server.test.HelloService;

public class HelloServiceImpl implements HelloService {
	private String str = "hi";

	@Override
	public String hi(String name) throws Exception {
		return str + " " + name;
	}
}
