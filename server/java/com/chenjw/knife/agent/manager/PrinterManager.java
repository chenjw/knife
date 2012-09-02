package com.chenjw.knife.agent.manager;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;

public class PrinterManager implements Lifecycle {

	private static final PrinterManager INSTANCE = new PrinterManager();

	public static PrinterManager getInstance() {
		return INSTANCE;
	}

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
