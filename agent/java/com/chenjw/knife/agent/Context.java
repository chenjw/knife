package com.chenjw.knife.agent;

import java.util.HashMap;
import java.util.Map;

public class Context {
	private boolean testBoolean = false;
	private byte testByte = 1;
	private char testChar = '2';
	private short testShort = 3;
	private int testInt = 4;
	private long testLong = 5;
	private float testFloat = 6;
	private double testDouble = 7;

	private String testStr = "8";
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
