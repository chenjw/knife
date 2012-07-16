package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import com.chenjw.knife.utils.JvmUtils;

public class AgentMain {
	private static Map<String, String> parse(String arguments) {
		Map<String, String> map = new HashMap<String, String>();
		for (String str : arguments.split("&")) {
			String[] strs = str.split("=");
			map.put(strs[0], strs[1]);
		}
		return map;
	}

	public static void agentmain(String arguments, Instrumentation inst)
			throws Exception {
		appendJar(inst);
		// inst.appendToBootstrapClassLoaderSearch(new JarFile(
		// "/home/chenjw/test/Test.jar"));
		Map<String, String> argumentMap = parse(arguments);
		Thread thread = new Thread(new AgentServer(Integer.parseInt(argumentMap
				.get("port")), inst), "agent-server");
		thread.setDaemon(true);
		thread.start();
		System.out.println("agent installed!");
	}

	private static void appendJar(Instrumentation inst) throws IOException {
		for (String path : JvmUtils.findJars()) {
			// System.out.println(file.getName());
			inst.appendToSystemClassLoaderSearch(new JarFile(path));
		}
	}
}
