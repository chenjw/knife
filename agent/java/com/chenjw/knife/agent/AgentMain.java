package com.chenjw.knife.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import com.chenjw.knife.utils.JarHelper;

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
		try {
			appendJar(inst);
			// inst.appendToBootstrapClassLoaderSearch(new JarFile(
			// "/home/chenjw/test/Test.jar"));

			// ClassFileTransformer transformer = new MyClassFileTransformer();
			// inst.addTransformer(transformer, true);
			// List<Class<?>> cList = new ArrayList<Class<?>>();
			// for (Class<?> clazz : inst.getAllLoadedClasses()) {
			// if (inst.isModifiableClass(clazz)) {
			// cList.add(clazz);
			// // System.out.println("+" + clazz.getName());
			// } else {
			//
			// }
			// }
			// inst.retransformClasses(Class.forName("$Proxy0"));

			// /
			Map<String, String> argumentMap = parse(arguments);
			Thread thread = new Thread(new AgentServer(
					Integer.parseInt(argumentMap.get("port")), inst),
					"agent-server");
			thread.setDaemon(true);
			thread.start();
			System.out.println("agent installed!");
		} catch (Throwable e) {
			storeException(e);
		}

	}

	public static void storeException(Throwable e) {
		File f = new File("/tmp/knife.log");
		try {
			f.createNewFile();
			e.printStackTrace(new PrintStream(new FileOutputStream(f)));
		} catch (Exception e1) {

		}

	}

	private static void appendJar(Instrumentation inst) throws IOException {
		for (String path : JarHelper.findJars()) {

			// System.out.println(path);
			// inst.appendToBootstrapClassLoaderSearch(new JarFile(path));
			inst.appendToSystemClassLoaderSearch(new JarFile(path));
		}
	}
}
