package com.chenjw.knife.agent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import com.chenjw.knife.agent.util.JarHelper;

public class AgentMain {

	private static void initJarPath(Instrumentation inst,
			Map<String, String> args) {
		try {
			String bjs = args.get("bootstrapJars");
			if (bjs != null) {
				String[] bjss = bjs.split(";");
				for (String s : bjss) {
					JarFile jar = new JarFile(s);
					if (!JarHelper.isLoaded(jar)) {
						inst.appendToBootstrapClassLoaderSearch(jar);
						System.out.println(s + " loaded");
					}
				}
			}
			String sjs = args.get("systemJars");
			if (sjs != null) {
				String[] sjss = sjs.split(";");
				for (String s : sjss) {
					JarFile jar = new JarFile(s);
					if (!JarHelper.isLoaded(jar)) {
						inst.appendToSystemClassLoaderSearch(jar);
						System.out.println(s + " loaded");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

			initJarPath(inst, args);

			AgentInfo agentInfo = new AgentInfo();
			agentInfo.setInst(inst);
			Thread thread = new Thread(new AgentServer(Integer.parseInt(args
					.get("port")), agentInfo), "agent-server");
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
}
