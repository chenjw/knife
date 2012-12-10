package com.chenjw.knife.agent.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;


public class ServiceRegistry {
	private static List<Lifecycle> services = new ArrayList<Lifecycle>();
	static {
		for (Lifecycle service : ServiceLoader.load(Lifecycle.class,
				ServiceRegistry.class.getClassLoader())) {
			services.add(service);
		}
	}

	public static void init() {
		for (Lifecycle service : services) {
			service.init();
		}
	}

	public static void clear() {
		for (Lifecycle service : services) {
			service.clear();
		}
	}

	public static void close() {
		for (Lifecycle service : services) {
			service.close();
		}
	}

}
