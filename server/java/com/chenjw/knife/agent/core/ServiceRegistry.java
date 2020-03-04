package com.chenjw.knife.agent.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 服务注册中心，所以注册的服务都可以从这里取到，并且保证所有服务的生命周期相关方法在相应的时候被调用
 * 
 * @author chenjw
 *
 */
public class ServiceRegistry {

	private static Map<Class<?>, Lifecycle> services = new LinkedHashMap<Class<?>, Lifecycle>();
	static {
	    // System.err.println("start load service");
		try {
			for (Lifecycle service : ServiceLoader.load(Lifecycle.class,
					ServiceRegistry.class.getClassLoader())) {
				services.put(service.getClass(), service);
				// System.err.println("load "+service.getClass());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// System.err.println("end load service");

	}

	@SuppressWarnings("unchecked")
	public static <T extends Lifecycle> T getService(Class<T> clazz) {
		return (T) services.get(clazz);
	}

	public static void init() {
		for (Lifecycle service : services.values()) {
			service.init();
		}
	}

	public static void clear() {
		for (Lifecycle service : services.values()) {
			service.clear();
		}
	}

	public static void close() {
		for (Lifecycle service : services.values()) {
			service.close();
		}
	}

}
