package com.chenjw.knife.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * jvm相关的工具类
 * 
 * @author chenjw
 * 
 */
public class JvmHelper {

	private static String pid;
	public final static String getPID() {
		if (pid == null) {
			String name = ManagementFactory.getRuntimeMXBean().getName();
			String[] items = name.split("@");
			pid = items[0];
		}
		return pid;
	}

}