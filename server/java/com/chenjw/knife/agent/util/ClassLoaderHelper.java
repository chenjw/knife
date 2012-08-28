package com.chenjw.knife.agent.util;

import com.chenjw.knife.agent.AgentClassLoader;

public class ClassLoaderHelper {
	public static void resetClassLoader(Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		ClassLoader currentClassLoader = Thread.currentThread()
				.getContextClassLoader();
		if (currentClassLoader instanceof AgentClassLoader) {
			AgentClassLoader loader = (AgentClassLoader) currentClassLoader;
			loader.setParent(clazz.getClassLoader());
		}
	}
}
