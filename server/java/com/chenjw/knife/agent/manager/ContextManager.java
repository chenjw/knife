package com.chenjw.knife.agent.manager;

import java.util.HashMap;
import java.util.Map;

public class ContextManager implements Lifecycle {
	private static final ContextManager INSTANCE = new ContextManager();

	private Map<String, Object> map = new HashMap<String, Object>();
	{
		SystemTagManager.getInstance().registerSystemTag("SYSTEM_CONTEXT_MAP",
				map);
	}

	public static ContextManager getInstance() {
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
