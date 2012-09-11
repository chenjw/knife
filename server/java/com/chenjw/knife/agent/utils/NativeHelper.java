package com.chenjw.knife.agent.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.utils.IOHelper;
import com.chenjw.knife.utils.StringHelper;

public class NativeHelper {
	static {
		// System.load("/home/chenjw/my_workspace/knife/native/src/.libs/libnativehelper.so");
		NativeHelper.loadNativeLibrary("libnativehelper");
	}
	private static Object[] retransformLock = new Object[0];
	private static String jvmClassName = null;

	private static byte[] classBytes = null;
	public static Printer printer;

	/**
	 * 加载so或dll包，为支持跨平台，入参应不包含后缀
	 * 
	 * @param libName
	 */
	public static void loadNativeLibrary(String libName) {
		String suffix = null;
		if (PlatformHelper.isLinux()) {
			suffix = ".so";
		} else if (PlatformHelper.isWindows()) {
			suffix = ".dll";
		} else {
			return;
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = NativeHelper.class.getResource("/" + libName + suffix)
					.openStream();
			File tmpFile = File.createTempFile(libName, suffix);
			tmpFile.deleteOnExit();
			os = new FileOutputStream(tmpFile);
			IOHelper.copy(is, os);
			System.load(tmpFile.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("load " + libName + " error!", e);
		} finally {
			IOHelper.closeQuietly(is);
			IOHelper.closeQuietly(os);
		}

	}

	public static Class<?> findLoadedClass(String className) {

		for (Class<?> clazz : Agent.getAllLoadedClasses()) {
			if (clazz.getName().equals(className)) {
				return clazz;
			}
		}
		return null;

	}

	public static List<ReferenceCount> countReferree(int num) {

		long[] counts = new long[num];
		Object[] objs = new Object[num];
		NativeHelper.countReferree0(num, counts, objs);
		List<ReferenceCount> result = new ArrayList<ReferenceCount>();
		for (int i = 0; i < num; i++) {
			ReferenceCount referenceCount = new ReferenceCount();
			referenceCount.setCount(counts[i]);
			referenceCount.setObj(objs[i]);
			result.add(referenceCount);
		}
		return result;
	}

	/**
	 * find instances by class
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] findInstancesByClass(Class<T> clazz) {
		Object[] objs = findInstancesByClass0(clazz);
		if (objs == null) {
			return null;
		}
		T[] r = (T[]) Array.newInstance(clazz, objs.length);
		for (int i = 0; i < objs.length; i++) {
			r[i] = (T) objs[i];
		}
		return r;
	}

	public static Field findStaticField(Class<?> clazz, String fieldName) {
		Field field = null;
		for (Field f : getStaticFields(clazz)) {
			if (fieldName.equals(f.getName())) {
				field = f;
				break;
			}
		}
		return field;
	}

	public static Field findField(Object obj, String fieldName) {
		Field field = null;
		for (Field f : getFields(obj.getClass())) {
			if (fieldName.equals(f.getName())) {
				field = f;
				break;
			}
		}
		return field;
	}

	public static Object findFieldValue(Object obj, String fieldName) {
		Field field = findField(obj, fieldName);
		if (field == null) {
			return null;
		} else {
			return getFieldValue(obj, field);
		}
	}

	public static Object findStaticFieldValue(Class<?> clazz, String fieldName) {
		Field field = findStaticField(clazz, fieldName);
		if (field == null) {
			return null;
		} else {
			return getStaticFieldValue(clazz, field);
		}
	}

	public static void setStaticFieldValue(Class<?> clazz, Field field,
			Object value) {
		if (field == null) {
			throw new RuntimeException("field cant be null!");
		}
		Class<?> type = field.getType();
		if (type == int.class) {
			setStaticIntFieldValue0(clazz, field, (Integer) value);
		} else if (type == short.class) {
			setStaticShortFieldValue0(clazz, field, (Short) value);
		} else if (type == long.class) {
			setStaticLongFieldValue0(clazz, field, (Long) value);
		} else if (type == double.class) {
			setStaticDoubleFieldValue0(clazz, field, (Double) value);
		} else if (type == float.class) {
			setStaticFloatFieldValue0(clazz, field, (Float) value);
		} else if (type == boolean.class) {
			setStaticBooleanFieldValue0(clazz, field, (Boolean) value);
		} else if (type == char.class) {
			setStaticCharFieldValue0(clazz, field, (Character) value);
		} else if (type == byte.class) {
			setStaticByteFieldValue0(clazz, field, (Byte) value);
		} else {
			setStaticObjectFieldValue0(clazz, field, value);
		}
	}

	public static void setStaticFieldValue(Class<?> clazz, String fieldName,
			Object value) {
		Field field = findStaticField(clazz, fieldName);
		setStaticFieldValue(clazz, field, value);
	}

	public static void setFieldValue(Object obj, Field field, Object value) {
		if (field == null) {
			throw new RuntimeException("field cant be null!");
		}
		Class<?> type = field.getType();
		if (type == int.class) {
			setIntFieldValue0(obj, field, (Integer) value);
		} else if (type == short.class) {
			setShortFieldValue0(obj, field, (Short) value);
		} else if (type == long.class) {
			setLongFieldValue0(obj, field, (Long) value);
		} else if (type == double.class) {
			setDoubleFieldValue0(obj, field, (Double) value);
		} else if (type == float.class) {
			setFloatFieldValue0(obj, field, (Float) value);
		} else if (type == boolean.class) {
			setBooleanFieldValue0(obj, field, (Boolean) value);
		} else if (type == char.class) {
			setCharFieldValue0(obj, field, (Character) value);
		} else if (type == byte.class) {
			setByteFieldValue0(obj, field, (Byte) value);
		} else {
			setObjectFieldValue0(obj, field, value);
		}
	}

	public static void setFieldValue(Object obj, String fieldName, Object value) {
		Field field = findField(obj, fieldName);
		setFieldValue(obj, field, value);
	}

	private static Object getFieldValue(Object obj, Field field) {
		Class<?> type = field.getType();
		if (type == int.class) {
			return getIntFieldValue0(obj, field);
		} else if (type == short.class) {
			return getShortFieldValue0(obj, field);
		} else if (type == long.class) {
			return getLongFieldValue0(obj, field);
		} else if (type == double.class) {
			return getDoubleFieldValue0(obj, field);
		} else if (type == float.class) {
			return getFloatFieldValue0(obj, field);
		} else if (type == boolean.class) {
			return getBooleanFieldValue0(obj, field);
		} else if (type == char.class) {
			return getCharFieldValue0(obj, field);
		} else if (type == byte.class) {
			return getByteFieldValue0(obj, field);
		} else {
			return getObjectFieldValue0(obj, field);
		}
	}

	public static Map<Field, Object> getFieldValues(Object obj) {
		Map<Field, Object> result = new HashMap<Field, Object>();
		if (obj != null) {
			for (Field field : getFields(obj.getClass())) {
				result.put(field, getFieldValue(obj, field));
			}
		}
		return result;
	}

	private static Object getStaticFieldValue(Class<?> clazz, Field field) {
		Class<?> type = field.getType();
		if (type == int.class) {
			return getStaticIntFieldValue0(clazz, field);
		} else if (type == short.class) {
			return getStaticShortFieldValue0(clazz, field);
		} else if (type == long.class) {
			return getStaticLongFieldValue0(clazz, field);
		} else if (type == double.class) {
			return getStaticDoubleFieldValue0(clazz, field);
		} else if (type == float.class) {
			return getStaticFloatFieldValue0(clazz, field);
		} else if (type == boolean.class) {
			return getStaticBooleanFieldValue0(clazz, field);
		} else if (type == char.class) {
			return getStaticCharFieldValue0(clazz, field);
		} else if (type == byte.class) {
			return getStaticByteFieldValue0(clazz, field);
		} else {
			return getStaticObjectFieldValue0(clazz, field);
		}
	}

	public static Map<Field, Object> getStaticFieldValues(Class<?> clazz) {
		Map<Field, Object> result = new HashMap<Field, Object>();
		if (clazz != null) {
			for (Field field : getStaticFields(clazz)) {
				result.put(field, getStaticFieldValue(clazz, field));
			}
		}
		return result;
	}

	private static Field[] getFields(Class<?> clazz) {
		Set<Field> fieldList = new HashSet<Field>();
		while (clazz != Object.class && clazz != null) {
			for (Field f : clazz.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					fieldList.add(f);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	private static Field[] getStaticFields(Class<?> clazz) {
		Set<Field> fieldList = new HashSet<Field>();
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())) {
				fieldList.add(f);
			}
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	public static String getClassSourceFileName(Class<?> clazz) {
		String str = getClassSourceFileName0(clazz);
		System.out.println("returnd " + str);
		return str;
	}

	public static byte[] getClassBytes(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		synchronized (retransformLock) {
			jvmClassName = StringHelper.replaceChars(clazz.getName(), ".", "/");
			classBytes = null;
			startClassFileLoadHook0();
			retransformClasses0(new Class<?>[] { clazz });
			stopClassFileLoadHook0();
			byte[] r = classBytes;
			jvmClassName = null;
			classBytes = null;
			return r;
		}
	}

	public static void redefineClass(Class<?> clazz, byte[] newClassBytes) {
		try {
			Agent.redefineClasses(clazz, newClassBytes);
			// Agent.println("<REDEFINE> " + clazz.getSimpleName());
		} catch (VerifyError e) {
			e.printStackTrace();
			// System.out.println("<ERROR> " + clazz.getSimpleName());
			// Agent.println("<ERROR> " + clazz.getSimpleName());
		}
		// redefineClass0(clazz, newClassBytes);
	}

	public static Object[] findReferrerByObject(Object obj) {
		return findReferrerByObject0(obj);
	}

	public static Object[] findReferreeByObject(Object obj) {
		return findReferreeByObject0(obj);
	}

	public static void startClassLoadHook() {
		startClassLoadHook0();
	}

	public static void stopClassLoadHook() {
		stopClassLoadHook0();
	}

	private native static void redefineClass0(Class<?> clazz,
			byte[] newClassBytes);

	private native static void startClassFileLoadHook0();

	private native static void stopClassFileLoadHook0();

	private native static void startClassLoadHook0();

	private native static void stopClassLoadHook0();

	private native static void retransformClasses0(Class<?>[] classes);

	private native static Object[] findInstancesByClass0(Class<?> clazz);

	private native static Object[] findReferrerByObject0(Object obj);

	private native static Object[] findReferreeByObject0(Object obj);

	private native static void countReferree0(int num, long[] nums,
			Object[] objs);

	private native static Object getFieldValue0(Object obj,
			Class<?> fieldClass, String name, Class<?> returnType);

	// ///////////////////////////////////////////////
	// set field
	// ///////////////////////////////////////////////
	private native static void setObjectFieldValue0(Object obj, Field field,
			Object newValue);

	private native static void setBooleanFieldValue0(Object obj, Field field,
			boolean newValue);

	private native static void setByteFieldValue0(Object obj, Field field,
			byte newValue);

	private native static void setCharFieldValue0(Object obj, Field field,
			char newValue);

	private native static void setShortFieldValue0(Object obj, Field field,
			short newValue);

	private native static void setIntFieldValue0(Object obj, Field field,
			int newValue);

	private native static void setLongFieldValue0(Object obj, Field field,
			long newValue);

	private native static void setFloatFieldValue0(Object obj, Field field,
			float newValue);

	private native static void setDoubleFieldValue0(Object obj, Field field,
			double newValue);

	// ///////////////////////////////////////////////
	// set static field
	// ///////////////////////////////////////////////
	private native static void setStaticObjectFieldValue0(Class<?> clazz,
			Field field, Object newValue);

	private native static void setStaticBooleanFieldValue0(Class<?> clazz,
			Field field, boolean newValue);

	private native static void setStaticByteFieldValue0(Class<?> clazz,
			Field field, byte newValue);

	private native static void setStaticCharFieldValue0(Class<?> clazz,
			Field field, char newValue);

	private native static void setStaticShortFieldValue0(Class<?> clazz,
			Field field, short newValue);

	private native static void setStaticIntFieldValue0(Class<?> clazz,
			Field field, int newValue);

	private native static void setStaticLongFieldValue0(Class<?> clazz,
			Field field, long newValue);

	private native static void setStaticFloatFieldValue0(Class<?> clazz,
			Field field, float newValue);

	private native static void setStaticDoubleFieldValue0(Class<?> clazz,
			Field field, double newValue);

	// ///////////////////////////////////////////////
	// get field
	// ///////////////////////////////////////////////
	private native static Object getObjectFieldValue0(Object obj, Field field);

	private native static boolean getBooleanFieldValue0(Object obj, Field field);

	private native static byte getByteFieldValue0(Object obj, Field field);

	private native static char getCharFieldValue0(Object obj, Field field);

	private native static short getShortFieldValue0(Object obj, Field field);

	private native static int getIntFieldValue0(Object obj, Field field);

	private native static long getLongFieldValue0(Object obj, Field field);

	private native static float getFloatFieldValue0(Object obj, Field field);

	private native static double getDoubleFieldValue0(Object obj, Field field);

	// ///////////////////////////////////////////////
	// get static field
	// ///////////////////////////////////////////////
	private native static Object getStaticObjectFieldValue0(Class<?> clazz,
			Field field);

	private native static boolean getStaticBooleanFieldValue0(Class<?> clazz,
			Field field);

	private native static byte getStaticByteFieldValue0(Class<?> clazz,
			Field field);

	private native static char getStaticCharFieldValue0(Class<?> clazz,
			Field field);

	private native static short getStaticShortFieldValue0(Class<?> clazz,
			Field field);

	private native static int getStaticIntFieldValue0(Class<?> clazz,
			Field field);

	private native static long getStaticLongFieldValue0(Class<?> clazz,
			Field field);

	private native static float getStaticFloatFieldValue0(Class<?> clazz,
			Field field);

	private native static double getStaticDoubleFieldValue0(Class<?> clazz,
			Field field);

	private native static String getClassSourceFileName0(Class<?> clazz);

	private native static ClassLoader getCallerClassLoader0();

	/**
	 * invoke by native code
	 * 
	 * @param loader
	 * @param classname
	 * @param classBeingRedefined
	 * @param protectionDomain
	 * @param classfileBuffer
	 * @param isRetransformer
	 * @return
	 */
	private static byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) {
		if (className != null && className.equals(jvmClassName)) {
			if (classBytes == null) {
				classBytes = classfileBuffer;
				return null;
			} else {
				return classBytes;
			}
		}
		return null;
	}

	/**
	 * invoke by native code
	 * 
	 * @param loader
	 * @param classname
	 * @param classBeingRedefined
	 * @param protectionDomain
	 * @param classfileBuffer
	 * @param isRetransformer
	 * @return
	 */
	private static void classLoaded(Class<?> clazz) {
		if (clazz.getClassLoader() == null) {
			if (printer != null) {
				printer.debug("[LOAD] " + clazz + " loaded by " + null);
			}
		} else {
			if (printer != null) {
				printer.debug("[LOAD] " + clazz + " loaded by "
						+ clazz.getClassLoader().getClass().getName());
			}
		}

	}

	private static void do1() throws ClassNotFoundException {
		int num = 10;
		Object[] objs = new Object[num];
		long[] counts = new long[num];

		NativeHelper.countReferree0(num, counts, objs);
		System.out.println(objs);
		System.out.println(objs.length);
		for (int i = 0; i < objs.length; i++) {
			System.out.println(counts[i] + " " + objs[i] + " "
					+ NativeHelper.findReferreeByObject0(objs[i]).length);

		}
		for (Object oo : NativeHelper.findReferreeByObject0(objs)) {
			System.out.println(">>" + oo);
		}

		System.out.println(objs[5] == objs[6]);

	}

	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchFieldException {
		do1();
		System.out.println("finished!");
	}

	public static class ReferenceCount {
		private long count;
		private Object obj;

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

	}
}
