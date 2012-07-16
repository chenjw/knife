package com.chenjw.knife.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class JvmUtils {

	private static final String LIB_FOLDER = "/home/chenjw/my_workspace/knife/dist/knife/lib/";
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

	public static String[] findJars() {
		File folder = findJarFolder();
		File[] fs = folder.listFiles();
		List<String> fns = new ArrayList<String>();
		for (File f : fs) {
			if (f.getName().endsWith(".jar")) {
				fns.add(f.getAbsolutePath());
			}
		}
		return fns.toArray(new String[fns.size()]);
	}

	private static File findJarFolder() {
		String agentPath = null;
		String tmp = JvmUtils.class.getClassLoader()
				.getResource("com/chenjw/knife").toString();
		String folder;
		if (tmp.indexOf("!") == -1) {
			folder = LIB_FOLDER;
		} else {
			tmp = tmp.substring(0, tmp.indexOf("!"));
			tmp = tmp.substring("jar:".length(), tmp.lastIndexOf("/"));
			folder = tmp;
			try {
				folder = new File(new URI(folder)).getAbsolutePath();
			} catch (URISyntaxException e) {
				throw new RuntimeException(agentPath + " not found!", e);
			}
		}
		return new File(folder);
	}

	public static String findJar(String fileName) {
		File folder = findJarFolder();
		File file = new File(folder, fileName);
		return file.getAbsolutePath();
	}

	public static void main(String[] args) {
		System.out.println(JvmUtils.findJar("knife-agent.jar"));
	}
}