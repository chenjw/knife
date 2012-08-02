package com.chenjw.knife.agent;

import java.util.HashMap;
import java.util.Map;

public class Context {
	public Object ccc = new Object();

	private static Map<String, Object> map = new HashMap<String, Object>();

	public static void put(String key, Object value) {
		map.put(key, value);
	}

	public static Object get(String key) {
		return map.get(key);
	}

	public static void clear() {
		map.clear();
	}
}
