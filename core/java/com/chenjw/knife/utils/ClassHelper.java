package com.chenjw.knife.utils;

/**
 * 工具类
 * 
 * @author chenjw 2012-6-14 上午12:19:48
 */
public class ClassHelper {
	/**
	 * 从基础类型获得封装类型
	 * 
	 * @param clazz
	 * @return 封装类型
	 */
	public static Class<?> getBoxClazz(Class<?> clazz) {
		if (!clazz.isPrimitive()) {
			return clazz;
		}
		if (clazz == int.class) {
			return Integer.class;
		} else if (clazz == long.class) {
			return Long.class;
		} else if (clazz == float.class) {
			return Float.class;
		} else if (clazz == double.class) {
			return Double.class;
		} else if (clazz == short.class) {
			return Short.class;
		} else if (clazz == boolean.class) {
			return Boolean.class;
		} else if (clazz == byte.class) {
			return Byte.class;
		} else if (clazz == char.class) {
			return Character.class;
		} else {
			return clazz;
		}
	}

	/**
	 * 根据class获得表示类型的字符串
	 * 
	 * @param c
	 * @return
	 */
	public static String makeClassName(Class<?> c) {
		if (c.isArray()) {
			StringBuilder sb = new StringBuilder();
			do {
				sb.append("[]");
				c = c.getComponentType();
			} while (c.isArray());

			return c.getName() + sb.toString();
		}
		return c.getName();
	}

	private static String getBasicJvmClassName(String name) {
		if ("boolean".equals(name)) {
			return "Z";
		} else if ("char".equals(name)) {
			return "C";
		} else if ("byte".equals(name)) {
			return "B";
		} else if ("short".equals(name)) {
			return "S";
		} else if ("int".equals(name)) {
			return "I";
		} else if ("long".equals(name)) {
			return "J";
		} else if ("float".equals(name)) {
			return "F";
		} else if ("double".equals(name)) {
			return "D";
		} else if ("void".equals(name)) {
			return "V";
		} else {
			return null;
		}
	}

	private static Class<?> findPrimitiveClass(String name) {
		if ("int".equals(name)) {
			return int.class;
		} else if ("long".equals(name)) {
			return long.class;
		} else if ("float".equals(name)) {
			return float.class;
		} else if ("short".equals(name)) {
			return short.class;
		} else if ("double".equals(name)) {
			return double.class;
		} else if ("char".equals(name)) {
			return char.class;
		} else if ("boolean".equals(name)) {
			return boolean.class;
		} else if ("byte".equals(name)) {
			return byte.class;
		} else if ("void".equals(name)) {
			return void.class;
		} else {
			return null;
		}
	}

	public static Class<?> findClass(String className) {

		Class<?> clazz = findPrimitiveClass(className);
		if (clazz == null) {
			// 数组类型的
			boolean isFirst = true;
			String as = "";
			// System.err.println(className);
			while (className.endsWith("[]")) {
				as += "[";
				className = StringHelper.substringBeforeLast(className, "[]");
				isFirst = false;
				// System.err.println(className);
			}
			if (!isFirst) {
				String basicJvmClassName = getBasicJvmClassName(className);
				if (basicJvmClassName == null) {
					className = as + "L" + className + ";";
				} else {
					className = as + basicJvmClassName;
				}
			}
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}

	/**
	 * 根据class调用默认构造函数创建实例
	 * 
	 * @param clazz
	 * @return
	 */
	public static Object newInstance(Class<?> clazz) {
		try {
			return clazz.getConstructor(new Class[0])
					.newInstance(new Object[0]);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据原始类型和目标类型判断是否需要解包
	 * 
	 * @param destClazz
	 * @return
	 */
	public static boolean isNeedUnbox(Class<?> originClazz, Class<?> destClazz) {
		if (destClazz.isPrimitive() && !originClazz.isPrimitive()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据原始类型和目标类型判断是否需要装包
	 * 
	 * @param originClazz
	 * @param destClazz
	 * @return
	 */
	public static boolean isNeedBox(Class<?> originClazz, Class<?> destClazz) {
		if (originClazz.isPrimitive() && !destClazz.isPrimitive()) {
			return true;
		} else {
			return false;
		}
	}

}
