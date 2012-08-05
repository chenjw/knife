package com.chenjw.knife.agent;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarHelper {
	public static boolean isLoaded(String s) {
		JarFile jar;
		try {
			jar = new JarFile(s);
		} catch (IOException e1) {
			return false;
		}
		Enumeration<JarEntry> en = jar.entries();
		while (en.hasMoreElements()) {
			JarEntry entry = en.nextElement();
			String name = entry.getName();
			if (name != null && name.endsWith(".class")) {
				String className = name.replaceAll("/", ".");
				className = className.substring(0, className.length() - 6);
				try {
					Class.forName(className);
				} catch (Throwable e) {
					return false;
				}
				return true;
			}
		}
		return false;

	}
}
