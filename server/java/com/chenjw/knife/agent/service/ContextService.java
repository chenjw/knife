package com.chenjw.knife.agent.service;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.core.ServiceRegistry;

public class ContextService implements Lifecycle {

	private Map<String, Object> map = new HashMap<String, Object>();

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}

	public void clear() {
		map.clear();
	}

	public void init() {
		ServiceRegistry.getService(SystemTagService.class).registerSystemTag(
				"SYSTEM_CONTEXT_MAP", map);
	}

	@Override
	public void close() {

	}
}
