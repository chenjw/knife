package com.chenjw.knife.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.apache.commons.io.FileUtils;

public class JvmUtils {
	private static final String PID_PATH = "/tmp/java_pid/";
	private static String pid;

	private static void mkDir(String path) {
		File folder = new File(path);
		try {
			FileUtils.forceMkdir(folder);
		} catch (IOException e) {
		}
	}

	public static void writePid(String key) {
		mkDir(PID_PATH);
		String pid = getPID();
		File f = new File(PID_PATH + File.pathSeparator + key);
		try {
			FileUtils.writeStringToFile(f, pid, "UTF-8");
		} catch (IOException e) {
		}
	}

	public static String findPid(String key) {
		mkDir(PID_PATH);
		File f = new File(PID_PATH + File.pathSeparator + key);
		if (f.exists()) {
			try {
				return FileUtils.readFileToString(f, "UTF-8");
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