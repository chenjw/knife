package com.chenjw.knife.agent.utils;

import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.core.Printer;

public class ClassLoaderHelper {
	public static Printer printer;

	public static void resetClassLoader(Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		ClassLoader currentClassLoader = Thread.currentThread()
				.getContextClassLoader();
		if (currentClassLoader instanceof AgentClassLoader) {
			AgentClassLoader loader = (AgentClassLoader) currentClassLoader;
			ClassLoader baseLoader = clazz.getClassLoader();
			if (baseLoader == null) {
				baseLoader = ClassLoader.getSystemClassLoader();
			}
			loader.setParent(baseLoader);
			if (printer != null) {
				printer.debug("reset currentClassLoader parent :"
						+ clazz.getClassLoader());
			}
		} else {
			if (printer != null) {
				printer.debug("currentClassLoader :"
						+ currentClassLoader.toString());
			}
		}
	}

	public static void view() {
		ClassLoader currentClassLoader = Thread.currentThread()
				.getContextClassLoader();
		if (printer != null) {
			printer.debug("view currentClassLoader :"
					+ currentClassLoader.toString());
		}
	}
}
