package com.chenjw.knife.agent.service;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.core.ServiceRegistry;

/**
 * 上下文服务，作用域全局，用来保存命令执行过程中的上下文，比如当前对象等
 * 
 * @author chenjw
 *
 */
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
