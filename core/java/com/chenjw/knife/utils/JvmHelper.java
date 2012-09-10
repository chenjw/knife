package com.chenjw.knife.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

public class JvmHelper {

	private static final String PID_PATH = "/tmp/java_pid/";
	private static String pid;

	private static void mkDir(String path) {
		File folder = new File(path);
		try {
			FileHelper.forceMkdir(folder);
		} catch (IOException e) {
		}
	}

	public static void writePid(String key) {
		mkDir(PID_PATH);
		String pid = getPID();
		File f = new File(PID_PATH + File.pathSeparator + key);
		try {
			FileHelper.writeStringToFile(f, pid, "UTF-8");
		} catch (IOException e) {
		}
	}

	public static String findPid(String key) {
		mkDir(PID_PATH);
		File f = new File(PID_PATH + File.pathSeparator + key);
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

	private final static String getPID1() {
		if (pid == null) {

			for (ThreadInfo id : ManagementFactory.getThreadMXBean()
					.dumpAllThreads(false, false)) {
				System.out.println(id.getThreadId());
				System.out.println(id.getStackTrace()[0].getClassName());
			}

		}
		return pid;
	}

	public static void main(String[] args) throws InterruptedException {
		JvmHelper.getPID1();
		while (true) {
			Thread.sleep(5000);
		}
	}

}