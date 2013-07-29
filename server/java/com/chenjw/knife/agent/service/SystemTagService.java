package com.chenjw.knife.agent.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.utils.NativeHelper;

/**
 * 用于记录一些系统对象，比如上下文等，这样在使用top,ref这些命令是如果返回的是系统对象就可以方便的标记出来
 * 
 * @author chenjw
 *
 */
public class SystemTagService implements Lifecycle {

	private Map<String, Object> systemTags = new HashMap<String, Object>();

	public String findSystemName(Object obj) {
		for (Entry<String, Object> entry : systemTags.entrySet()) {
			Object o = entry.getValue();
			if (obj == o) {
				return entry.getKey();
			} else if (o instanceof HashMap) {
				Object oo = NativeHelper.findFieldValue(o, "table");
				if (oo == obj) {
					return entry.getKey() + ".table";
				}
			} else if (o instanceof ArrayList) {
				Object oo = NativeHelper.findFieldValue(o, "elementData");
				if (oo == obj) {
					return entry.getKey() + ".elementData";
				}
			} else if (o instanceof IdentityHashMap) {
				Object oo = NativeHelper.findFieldValue(o, "table");
				if (oo == obj) {
					return entry.getKey() + ".table";
				}
			}
		}
		return null;
	}

	public void registerSystemTag(String name, Object obj) {
		systemTags.put(name, obj);
	}

	@Override
	public void init() {
		registerSystemTag("SYSTEM_TAGS", systemTags);
	}

	@Override
	public void clear() {

	}

	@Override
	public void close() {

	}

}
