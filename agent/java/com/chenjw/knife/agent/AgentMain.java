package com.chenjw.knife.agent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentMain {

	private static URL[] initJarPath(Map<String, String> args) {
		List<URL> result = new ArrayList<URL>();
		try {
			// String bjs = args.get("bootstrapJars");
			// if (bjs != null) {
			// String[] bjss = bjs.split(";");
			// for (String s : bjss) {
			// if (!JarHelper.isLoaded(s)) {
			// result.add(new URL("file", "", s));
			// // inst.appendToBootstrapClassLoaderSearch(jar);
			// System.out.println(s + " loaded");
			// }
			// }
			// }
			String sjs = args.get("systemJars");
			if (sjs != null) {
				String[] sjss = sjs.split(";");
				for (String s : sjss) {
					if (!JarHelper.isLoaded(s)) {
						result.add(new URL("file", "", s));
						// inst.appendToSystemClassLoaderSearch(jar);
						System.out.println(s + " loaded");
					}
				}
			}
			return result.toArray(new URL[result.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Map<String, String> parseArgs(String arguments) {
		Map<String, String> r = new HashMap<String, String>();
		if (arguments != null && arguments.length() > 0) {
			String[] strs = arguments.split("&");
			for (String str : strs) {
				String[] ss = str.split("=");
				if (ss.length == 2) {
					r.put(ss[0], ss[1]);
				}
			}
		}
		return r;
	}

	private static String readArgs(String fileName) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte buffer[] = new byte[4096];
			for (int n = 0; -1 != (n = fis.read(buffer));) {
				output.write(buffer, 0, n);
			}
			return new String(output.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void agentmain(String arguments, Instrumentation inst)
			throws Exception {

		try {

			Map<String, String> args = parseArgs(readArgs(arguments));
			URL[] urls = initJarPath(args);
			AgentClassLoader.setAgentClassLoader(new AgentClassLoader(urls,
					ClassLoader.getSystemClassLoader()));
			// Thread.currentThread().setContextClassLoader(classLoader);
			Class<?> classAgentServer = AgentClassLoader.getAgentClassLoader()
					.loadClass("com.chenjw.knife.agent.AgentServer");
			Constructor<?> constructor = classAgentServer
					.getConstructor(new Class<?>[] { int.class,
							Instrumentation.class });
			Thread thread = new Thread((Runnable) constructor.newInstance(
					Integer.parseInt(args.get("port")), inst), "agent-server");
			thread.setDaemon(true);
			thread.start();
			System.out.println("agent installed!");
		} catch (Throwable e) {
			e.printStackTrace();
			storeException(e);
		} finally {

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
}
