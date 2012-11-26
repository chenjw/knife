package com.chenjw.knife.agent.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.chenjw.knife.agent.utils.invoke.InvokeResult;
import com.chenjw.knife.agent.utils.invoke.MethodInvokeException;

public class ReflectHelper {
	private static ClassLoader getClassLoader(ClassLoader classLoader) {
		if (classLoader != null) {
			return classLoader;
		} else {
			classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null) {
				return classLoader;
			}
			return ReflectHelper.class.getClassLoader();
		}
	}

	private static Class<?> findClass(Object clazz, ClassLoader classLoader)
			throws MethodInvokeException {
		if (clazz == null) {
			throw new MethodInvokeException("class is null");
		}
		if (clazz instanceof Class) {
			return (Class<?>) clazz;
		} else if (clazz instanceof String) {
			Class<?> c;
			try {
				c = getClassLoader(classLoader).loadClass((String) clazz);
			} catch (ClassNotFoundException e) {
				throw new MethodInvokeException("class not found : " + clazz);
			}
			return c;
		} else {
			throw new MethodInvokeException("not support class : " + clazz);
		}
	}

	public static InvokeResult invokeConstructor(Object obj,
			Object[] parameterTypes, Object[] parameters,
			ClassLoader classLoader) throws MethodInvokeException {
		Class<?> clazz = findClass(obj, classLoader);

		Class<?>[] types = new Class<?>[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			types[i] = findClass(parameterTypes[i], classLoader);
		}
		InvokeResult result = new InvokeResult();
		try {
			Constructor<?> constructor = clazz.getConstructor(types);
			if (constructor == null) {
				throw new MethodInvokeException("constructor not found");
			}
			Object r = constructor.newInstance(parameters);
			result.setResult(r);
		} catch (InvocationTargetException e) {
			result.setE(e.getTargetException());
		} catch (Exception e) {
			throw new MethodInvokeException("invoke error", e);
		}
		return result;
	}

	public static InvokeResult invokeStaticMethod(Object obj,
			String methodName, Object[] parameterTypes, Object[] parameters,
			ClassLoader classLoader) throws MethodInvokeException {
		Class<?> clazz = findClass(obj, classLoader);

		Class<?>[] types = new Class<?>[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			types[i] = findClass(parameterTypes[i], classLoader);
		}
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, types);
		} catch (Exception e) {
			throw new MethodInvokeException("method not found", e);
		}
		return invokeStaticMethod(method, parameters);
	}

	public static InvokeResult invokeStaticMethod(Method method,
			Object[] parameters) throws MethodInvokeException {
		if (!Modifier.isStatic(method.getModifiers())) {
			throw new MethodInvokeException("method is not static");
		}
		boolean isAccessible = method.isAccessible();
		InvokeResult result = new InvokeResult();
		try {
			if (!isAccessible) {
				method.setAccessible(true);
			}
			Object r = method.invoke(null, parameters);
			result.setResult(r);
		} catch (InvocationTargetException e) {
			result.setE(e.getTargetException());
		} catch (Exception e) {
			throw new MethodInvokeException("invoke error", e);
		} finally {
			if (!isAccessible) {
				method.setAccessible(false);
			}
		}
		return result;
	}

	public static InvokeResult invokeMethod(Object obj, String methodName,
			Object[] parameterTypes, Object[] parameters,
			ClassLoader classLoader) throws MethodInvokeException {
		Class<?>[] types = new Class<?>[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			types[i] = findClass(parameterTypes[i], classLoader);
		}
		Method method = null;
		try {
			method = obj.getClass().getMethod(methodName, types);
		} catch (Exception e) {
			throw new MethodInvokeException("method not found", e);
		}
		return invokeMethod(obj, method, parameters);

	}

	public static InvokeResult invokeMethod(Object obj, Method method,
			Object[] parameters) throws MethodInvokeException {

		if (Modifier.isStatic(method.getModifiers())) {
			throw new MethodInvokeException("method is static");
		}
		boolean isAccessible = method.isAccessible();
		InvokeResult result = new InvokeResult();
		try {
			if (!isAccessible) {
				method.setAccessible(true);
			}
			Object r = method.invoke(obj, parameters);
			result.setResult(r);
		} catch (InvocationTargetException e) {
			result.setE(e.getTargetException());
		} catch (Exception e) {
			throw new MethodInvokeException("invoke error", e);
		} finally {
			if (!isAccessible) {
				method.setAccessible(false);
			}
		}
		return result;
	}

	public static Method[] getMethods(Class<?> clazz) {
		Set<Method> ms = new HashSet<Method>();
		for (Method m : clazz.getMethods()) {
			ms.add(m);
		}
		for (Method m : clazz.getDeclaredMethods()) {
			ms.add(m);
		}
		return ms.toArray(new Method[ms.size()]);
	}
}
