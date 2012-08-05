package com.chenjw.knife.agent.service;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager implements Lifecycle {
	private static final ServiceManager INSTANCE = new ServiceManager();

	private List<Lifecycle> services = new ArrayList<Lifecycle>();

	public static ServiceManager getInstance() {
		return INSTANCE;
	}

	public void register(Lifecycle service) {
		services.add(service);
	}

	@Override
	public void init() {
		for (Lifecycle service : services) {
			service.init();
		}

	}

	@Override
	public void clear() {
		for (Lifecycle service : services) {
			service.clear();
		}

	}

}
