package com.chenjw.knife.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.chenjw.knife.server.test.TestService;
import com.chenjw.knife.server.test.impl.TestServiceImpl;

public class NativeHelper {
	static {
		// System.load("/home/chenjw/my_workspace/knife/src/main/cpp/src/.libs/libnativehelper.so");
		// System.loadLibrary("libnativehelper");
		NativeHelper.loadNativeLibrary("libnativehelper");

	}

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
	 * 查找该类型的对象（包括继承自该类型的对象）
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
				Object value = getFieldValue0(obj, field.getName(),
						field.getType());
				result.put(field, value);
			}
		}
		return result;
	}

	private static Field[] getFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}

	private native static Object[] findInstancesByClass0(Class<?> clazz);

	private native static Object getFieldValue0(Object obj, String name,
			Class<?> clazz);

	public static void main(String[] args) {
		TestService i = new TestServiceImpl();
		for (TestService obj : NativeHelper
				.findInstancesByClass(TestService.class)) {
			// System.out.println(obj);
			// System.err.println(NativeHelper.getFieldValue0(obj,
			// "test1Service",
			// Test1Service.class));
			for (Entry<Field, Object> entry : NativeHelper.getFieldValues(obj)
					.entrySet()) {
				System.err.println(entry.getKey().getName() + "="
						+ entry.getValue());

			}
		}
	}
}
