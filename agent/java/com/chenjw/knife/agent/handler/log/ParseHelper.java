package com.chenjw.knife.agent.handler.log;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Agent;

public class ParseHelper {

	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Serializable) {
			try {
				return JSON.toJSONString(obj);
			} catch (Throwable e) {
				return obj.toString();
			}
		} else {
			return obj.toString();
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
		return obj;
	}

	public static Object[] parseMethodArgs(String expr, Class<?>[] types) {
		Object[] objs = new Object[types.length];
		int i = 0;
		for (Class<?> type : types) {
			int end = getFirstArgIndex(expr);
			String s = StringUtils.substring(expr, 0, end);
			objs[i] = ParseHelper.parseValue(s, type);
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
