package com.chenjw.knife.agent.utils;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import com.chenjw.knife.agent.manager.SystemTagManager;

public class ToStringHelper {

	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		String name = SystemTagManager.getInstance().findSystemName(obj);
		if (name != null) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		Map<Object, String> objMap = new IdentityHashMap<Object, String>();
		_toString(obj, sb, objMap, "$this", false);
		return sb.toString();
	}

	private static void _toString(Object obj, StringBuilder sb,
			Map<Object, String> objMap, String path, boolean isDetail) {

		if (obj == null) {
			sb.append("null");
		} else {
			String name;
			if ((name = objMap.get(obj)) != null) {
				sb.append(name);
			} else {
				objMap.put(obj, path);
				if (obj instanceof String) {
					sb.append("\"").append(obj.toString()).append("\"");
				} else if (obj instanceof Character) {
					sb.append("\'").append(obj.toString()).append("\'");
				} else if (obj instanceof Number) {
					sb.append(obj.toString());
				} else if (obj instanceof Boolean) {
					sb.append(obj.toString());
				} else if (obj instanceof Date) {
					sb.append(obj.toString());
				} else if (obj.getClass().isEnum()) {
					sb.append(obj.toString());
				} else {
					if (isDetail) {
						if (obj.getClass().isArray()) {
							_arrayToString(obj, sb, objMap, path, isDetail);
						} else if (obj instanceof Iterable) {
							_iterableToString(obj, sb, objMap, path, isDetail);
						} else if (obj instanceof Map) {
							_mapToString(obj, sb, objMap, path, isDetail);
						} else if (obj instanceof Map.Entry) {
							_entryToString(obj, sb, objMap, path, isDetail);
						} else {
							_getClassString(obj, sb);
						}
					} else {
						_getClassString(obj, sb);
					}
				}
			}
		}
	}

	private static void _arrayToString(Object obj, StringBuilder sb,
			Map<Object, String> objMap, String path, boolean isDetail) {
		sb.append("[");
		for (int i = 0; i < Array.getLength(obj); i++) {
			if (i != 0) {
				sb.append(",");
			}
			_toString(Array.get(obj, i), sb, objMap, path + "[" + i + "]",
					isDetail);
		}
		sb.append("]");
	}

	private static void _iterableToString(Object obj, StringBuilder sb,
			Map<Object, String> objMap, String path, boolean isDetail) {
		sb.append("[");
		Iterator<?> iterator = ((Iterable<?>) obj).iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (i != 0) {
				sb.append(",");
			}
			_toString(iterator.next(), sb, objMap, path + "[" + i + "]",
					isDetail);
			i++;
		}
		sb.append("]");
	}

	private static void _mapToString(Object obj, StringBuilder sb,
			Map<Object, String> objMap, String path, boolean isDetail) {
		sb.append("{");
		Iterator<?> iterator = ((Map<?, ?>) obj).entrySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (i != 0) {
				sb.append(",");
			}
			_toString(iterator.next(), sb, objMap, path + "[" + i + "]",
					isDetail);
			i++;
		}
		sb.append("}");
	}

	private static void _entryToString(Object obj, StringBuilder sb,
			Map<Object, String> objMap, String path, boolean isDetail) {
		Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
		_toString(entry.getKey(), sb, objMap, path + ".k", isDetail);
		sb.append("=");
		_toString(entry.getValue(), sb, objMap, path + ".v", isDetail);
	}

	private static void _getClassString(Object obj, StringBuilder sb) {
		sb.append("<").append(obj.getClass().getSimpleName()).append(">");
	}

	public static String toDetailString(Object obj) {
		if (obj == null) {
			return null;
		}
		String name = SystemTagManager.getInstance().findSystemName(obj);
		if (name != null) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		Map<Object, String> objMap = new IdentityHashMap<Object, String>();
		_toString(obj, sb, objMap, "$this", true);
		return sb.toString();
	}

}
