package com.chenjw.knife.server.test.impl;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.server.test.ApplyModel;
import com.chenjw.knife.server.test.DataService;

public class DataServiceImpl implements DataService {
	private Map<Integer, ApplyModel> msgs = new HashMap<Integer, ApplyModel>();

	@Override
	public ApplyModel find(int id) {
		return msgs.get(id);
	}

	@Override
	public void insert(ApplyModel apply) {
		msgs.put(apply.getId(), apply);
	}

	@Override
	public void update(ApplyModel apply) {
		msgs.get(apply.getId()).setMsg(apply.getMsg());
	}
}
