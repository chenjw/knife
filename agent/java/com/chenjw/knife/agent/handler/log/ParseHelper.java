package com.chenjw.knife.agent.handler.log;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Agent;

public class ParseHelper {

	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		String objStr = null;
		if (obj instanceof Collection || obj instanceof Entry) {
			objStr = obj.getClass().getName();
		} else {
			objStr = obj.toString() + " " + getClassString(obj);
		}
		return objStr;
	}

	private static String getClassString(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return "[" + obj.getClass().getSimpleName() + "]";
		}
	}

	public static String toJsonString(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Serializable) {
			try {
				return JSON.toJSONString(obj);
			} catch (Throwable e) {
				return toString(obj);
			}
		} else {
			return toString(obj);
		}
	}

	public static Object parseValue(String expr, Class<?> type) {
		Object obj = null;
		if (expr.startsWith("@")) {
			int num = Integer.parseInt(StringUtils.substringAfter(expr, "@")
					.trim());
			obj = InvokeRecord.get(num);
			if (obj == null) {
				Agent.println("object " + expr + " not found!");
			}
		} else {
			obj = JSON.parseObject(expr, type);
		}
		if (obj != null && !(type.isInstance(obj))) {
			throw new java.lang.IllegalArgumentException("cant parse expr ["
					+ expr + "] to type [" + type.getName() + "]");
		}
		return obj;
	}

	public static Object[] parseMethodArgs(String expr, Class<?>[] types) {
		Object[] objs = new Object[types.length];
		int i = 0;
		for (Class<?> type : types) {
			int end = getFirstArgIndex(expr);
			String s = StringUtils.substring(expr, 0, end);
			Object value = ParseHelper.parseValue(s, type);
			objs[i] = value;
			expr = StringUtils.substring(expr, end + 1);
			i++;
		}
		return objs;
	}

	private static int getFirstArgIndex(String str) {
		int n = 0;
		int i = 0;
		for (char c : str.toCharArray()) {
			if (c == '(') {
				n++;
			} else if (c == ')') {
				n--;
			} else if (c == '[') {
				n++;
			} else if (c == ']') {
				n--;
			} else if (c == '{') {
				n++;
			} else if (c == '}') {
				n--;
			} else if (c == ',') {
				if (n == 0) {
					return i;
				}
			}
			i++;
		}
		return i;
	}

}
