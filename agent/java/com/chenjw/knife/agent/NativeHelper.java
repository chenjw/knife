package com.chenjw.knife.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class NativeHelper {
	static {
		// System.load("/home/chenjw/my_workspace/knife/native/src/.libs/libnativehelper.so");
		NativeHelper.loadNativeLibrary("libnativehelper");
	}
	private static Object[] retransformLock = new Object[0];
	private static String jvmClassName = null;

	private static byte[] classBytes = null;

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
			IOUtils.copy(is, os);
			System.load(tmpFile.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("load " + libName + " error!", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}

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

	public static Map<Field, Object> getFieldValues(Object obj) {
		Map<Field, Object> result = new HashMap<Field, Object>();
		if (obj != null) {
			for (Field field : getFields(obj.getClass())) {
				Class<?> type = field.getType();
				if (type == int.class) {
					result.put(field, getIntFieldValue0(obj, field));
				} else if (type == short.class) {
					result.put(field, getShortFieldValue0(obj, field));
				} else if (type == long.class) {
					result.put(field, getLongFieldValue0(obj, field));
				} else if (type == double.class) {
					result.put(field, getDoubleFieldValue0(obj, field));
				} else if (type == float.class) {
					result.put(field, getFloatFieldValue0(obj, field));
				} else if (type == boolean.class) {
					result.put(field, getBooleanFieldValue0(obj, field));
				} else if (type == char.class) {
					result.put(field, getCharFieldValue0(obj, field));
				} else if (type == byte.class) {
					result.put(field, getByteFieldValue0(obj, field));
				} else {
					result.put(field, getObjectFieldValue0(obj, field));
				}
			}
		}
		return result;
	}

	public static Map<Field, Object> getStaticFieldValues(Class<?> clazz) {
		Map<Field, Object> result = new HashMap<Field, Object>();
		if (clazz != null) {
			for (Field field : getStaticFields(clazz)) {
				Class<?> type = field.getType();
				if (type == int.class) {
					result.put(field, getStaticIntFieldValue0(clazz, field));
				} else if (type == short.class) {
					result.put(field, getStaticShortFieldValue0(clazz, field));
				} else if (type == long.class) {
					result.put(field, getStaticLongFieldValue0(clazz, field));
				} else if (type == double.class) {
					result.put(field, getStaticDoubleFieldValue0(clazz, field));
				} else if (type == float.class) {
					result.put(field, getStaticFloatFieldValue0(clazz, field));
				} else if (type == boolean.class) {
					result.put(field, getStaticBooleanFieldValue0(clazz, field));
				} else if (type == char.class) {
					result.put(field, getStaticCharFieldValue0(clazz, field));
				} else if (type == byte.class) {
					result.put(field, getStaticByteFieldValue0(clazz, field));
				} else {
					result.put(field, getStaticObjectFieldValue0(clazz, field));
				}
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

	public static byte[] getClassBytes(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		synchronized (retransformLock) {
			jvmClassName = StringUtils.replaceChars(clazz.getName(), ".", "/");
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

	private native static void redefineClass0(Class<?> clazz,
			byte[] newClassBytes);

	private native static void startClassFileLoadHook0();

	private native static void stopClassFileLoadHook0();

	private native static void retransformClasses0(Class<?>[] classes);

	private native static Object[] findInstancesByClass0(Class<?> clazz);

	private native static Object[] findReferrerByObject0(Object obj);

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

	private static void do1() throws ClassNotFoundException {
		System.out
				.println(NativeHelper.getClassBytes(OutputStream.class).length);

		Class.forName("com.chenjw.knife.agent.handler.arg.Args");

	}

	private static void do2() throws ClassNotFoundException, SecurityException,
			NoSuchFieldException {

		Context ai = new Context();
		Object dd = ai.ccc;
		// System.out.println(ai.ccc);
		// System.out.println(ai);
		for (Object entry : NativeHelper.findReferrerByObject0(ai)) {
			// if (entry == ai) {
			// System.err.println(entry);
			// }
			// if (entry == ai.ccc) {
			// System.err.println(entry);
			// }
			System.err.println(entry);
		}

	}

	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchFieldException {
		do2();
		System.out.println("finished!");
	}
}
