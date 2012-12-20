package com.chenjw.knife.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * jar包相关的工具类
 * 
 * @author chenjw
 * 
 */
public class JarHelper {
	private static final String LIB_FOLDER = "../dist/knife/lib/";

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

	/**
	 * 
	 * 判断当前环境是否是开发环境
	 * 
	 * @return
	 */
	public static boolean isDevMode() {
		String tmp = JvmHelper.class.getClassLoader()
				.getResource("com/chenjw/knife").toString();
		return tmp.indexOf("!") == -1;
	}

	private static File findJarFolder() {
		String agentPath = null;
		String tmp = JvmHelper.class.getClassLoader()
				.getResource("com/chenjw/knife").toString();
		String folder;
		if (tmp.indexOf("!") == -1) {
			// 针对开发环境非jar包启动的情况
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

	public static String getToolsJarPath() {
		String components[] = System.getProperty("java.class.path").split(
				File.pathSeparator);
		String arr$[] = components;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			String c = arr$[i$];
			if (c.endsWith("tools.jar"))
				return (new File(c)).getAbsolutePath();
			if (c.endsWith("classes.jar"))
				return (new File(c)).getAbsolutePath();
		}

		if (System.getProperty("os.name").startsWith("Mac")) {
			String java_home = System.getProperty("java.home");
			String java_mac_home = java_home.substring(0,
					java_home.indexOf("/Home"));
			return (new StringBuilder()).append(java_mac_home)
					.append("/Classes/classes.jar").toString();
		} else {
			return (new StringBuilder())
					.append(System.getProperty("java.home"))
					.append("../lib/tools.jar").toString();
		}
	}
}
