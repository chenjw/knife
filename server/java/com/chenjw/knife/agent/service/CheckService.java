package com.chenjw.knife.agent.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.jvmti.Capabilitie;
import com.chenjw.knife.utils.StringHelper;

/**
 * 用来检测当前java虚拟机对于jvmti接口的实现情况
 * 
 * @author chenjw
 *
 */
public class CheckService implements Lifecycle {

	@Override
	public void init() {
		checkCapabilitie();
	}

	private void checkCapabilitie() {
		List<Capabilitie> r = new ArrayList<Capabilitie>();
		Map<Capabilitie, Boolean> caps = NativeHelper.checkCapabilities();
		for (Entry<Capabilitie, Boolean> entry : caps.entrySet()) {
			if (entry.getKey().isNeed() && !entry.getValue()) {
				r.add(entry.getKey());
			}
		}
		if (r.size() > 0) {
			Agent.info("not supported capabilities : "
					+ StringHelper.join(r, ","));
		}
	}

	@Override
	public void clear() {

	}

	@Override
	public void close() {

	}
}
