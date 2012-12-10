package com.chenjw.knife.agent.service;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.agent.core.Lifecycle;

public class ContextService implements Lifecycle {
	private static final ContextService INSTANCE = new ContextService();

	private Map<String, Object> map = new HashMap<String, Object>();
	{
		SystemTagService.getInstance().registerSystemTag("SYSTEM_CONTEXT_MAP",
				map);
	}

	public static ContextService getInstance() {
		return INSTANCE;
	}

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
	}

	@Override
	public void close() {

	}
}
