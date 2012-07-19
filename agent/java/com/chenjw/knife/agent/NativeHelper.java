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
		System.load("/home/chenjw/my_workspace/knife/native/src/.libs/libnativehelper.so");

		// NativeHelper.loadNativeLibrary("libnativehelper");

	}
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

	public static Map<Field, Object> getFieldValues(Object obj) {
		Map<Field, Object> result = new HashMap<Field, Object>();
		if (obj != null) {
			for (Field field : getFields(obj.getClass())) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				try {
					Object value = getFieldValue0(obj,
							field.getDeclaringClass(), field.getName(),
							field.getType());
					result.put(field, value);
				} catch (Exception e) {
					System.out.println(field + " not found");
				}

			}
		}
		return result;
	}

	private static Field[] getFields(Class<?> clazz) {
		Set<Field> fieldList = new HashSet<Field>();
		while (clazz != Object.class && clazz != null) {
			for (Field f : clazz.getDeclaredFields()) {
				fieldList.add(f);
			}
			clazz = clazz.getSuperclass();
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	public synchronized static byte[] getClassBytes(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		jvmClassName = StringUtils.replaceChars(clazz.getName(), ".", "/");
		startClassFileLoadHook0();
		retransformClasses(new Class<?>[] { clazz });
		stopClassFileLoadHook0();
		byte[] r = classBytes;
		jvmClassName = null;
		classBytes = null;
		return r;
	}

	private native static Object[] findInstancesByClass0(Class<?> clazz);

	private native static Object getFieldValue0(Object obj,
			Class<?> fieldClass, String name, Class<?> returnType);

	private native static void startClassFileLoadHook0();

	private native static void stopClassFileLoadHook0();

	private native static void retransformClasses(Class<?>[] classes);

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
			classBytes = classfileBuffer;
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

		Class c = Class.forName("com.chenjw.knife.agent.handler.arg.Args")
				.getDeclaredField("argStr").getDeclaringClass();
		System.out.println(c);
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchFieldException {
		do2();
		System.out.println("finished!");
	}
}
