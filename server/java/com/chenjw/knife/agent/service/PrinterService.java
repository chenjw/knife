package com.chenjw.knife.agent.service;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;

public class PrinterService implements Lifecycle {

	public void clear() {

	}

	public void init() {
		ClassLoaderHelper.printer = Agent.printer;
		NativeHelper.printer = Agent.printer;
	}

	@Override
	public void close() {
		ClassLoaderHelper.printer = null;
		NativeHelper.printer = null;
	}
}
