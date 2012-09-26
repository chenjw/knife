package com.chenjw.knife.agent.manager;

import com.chenjw.knife.agent.utils.NativeHelper;

public class Registry implements Lifecycle {
	private static final Registry INSTANCE = new Registry();

	public static Registry getInstance() {
		return INSTANCE;
	}

	@Override
	public void init() {
		Lifecycle[] services = NativeHelper
				.findInstancesByClass(Lifecycle.class);
		for (Lifecycle service : services) {
			if (service != INSTANCE) {
				service.init();
			}
		}
	}

	@Override
	public void clear() {
		Lifecycle[] services = NativeHelper
				.findInstancesByClass(Lifecycle.class);
		for (Lifecycle service : services) {
			if (service != INSTANCE) {
				service.clear();
			}
		}

	}

	@Override
	public void close() {
		Lifecycle[] services = NativeHelper
				.findInstancesByClass(Lifecycle.class);
		for (Lifecycle service : services) {
			if (service != INSTANCE) {
				service.close();
			}
		}
	}

}
