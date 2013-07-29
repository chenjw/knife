package com.chenjw.knife.agent.service;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;

/**
 * 用于在初始化时，初始化系统默认输出方式
 * 
 * @author chenjw
 *
 */
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
