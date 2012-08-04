package com.chenjw.knife.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class JarHelper {
	private static final String LIB_FOLDER = "/home/chenjw/my_workspace/knife/dist/knife/lib/";

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
		String tmp = JvmHelper.class.getClassLoader()
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
}
