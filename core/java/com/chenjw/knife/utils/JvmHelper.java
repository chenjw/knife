package com.chenjw.knife.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class JvmHelper {

	private static File PID_PATH = null;
	{
		try {
			PID_PATH = new File(File.createTempFile("_", "_").getParentFile(),
					"java_pid");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static String pid;

	private static void mkDir(File path) {
		try {
			FileHelper.forceMkdir(path);
		} catch (IOException e) {
		}
	}

	public static void writePid(String key) {
		mkDir(PID_PATH);
		String pid = getPID();

		File f = new File(PID_PATH, key);
		try {
			FileHelper.writeStringToFile(f, pid, "UTF-8");
		} catch (IOException e) {
		}
	}

	public static String findPid(String key) {
		mkDir(PID_PATH);
		File f = new File(PID_PATH, key);
		if (f.exists()) {
			try {
				return FileHelper.readFileToString(f, "UTF-8");
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public final static String getPID() {
		if (pid == null) {
			String name = ManagementFactory.getRuntimeMXBean().getName();
			String[] items = name.split("@");
			pid = items[0];
		}
		return pid;
	}

}