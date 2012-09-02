package com.chenjw.knife.agent.manager;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ObjectRecordManager implements Lifecycle {
	private static final ObjectRecordManager INSTANCE = new ObjectRecordManager();

	private Record record = new Record();

	public static ObjectRecordManager getInstance() {
		return INSTANCE;
	}

	private Record getRecord() {
		return record;
	}

	public int record(Object obj) {
		Record r = getRecord();
		return r.add(obj);
	}

	public String toId(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return "@" + record(obj) + " ";
		}
	}

	public Object get(int num) {

		Record r = getRecord();
		return r.get(num);
	}

	private static class Record {
		private List<Object> list = new ArrayList<Object>();

		private Map<Object, Integer> map = new IdentityHashMap<Object, Integer>();
		{
			SystemTagManager.getInstance().registerSystemTag(
					"SYSTEM_RECORD_LIST", list);
			SystemTagManager.getInstance().registerSystemTag(
					"SYSTEM_RECORD_MAP", map);
		}

		public void clear() {
			list.clear();
			map.clear();
		}

		public Object get(int i) {
			if (i >= list.size() || i < 0) {
				return null;
			}
			return list.get(i);
		}

		public int add(Object obj) {
			if (obj == null) {
				return -1;
			}
			Integer i = map.get(obj);
			if (i == null) {
				list.add(obj);
				i = list.size() - 1;
				map.put(obj, i);
				return i;
			}
			return i;
		}
	}

	@Override
	public void init() {

	}

	@Override
	public void clear() {
		record.clear();

	}

	@Override
	public void close() {

	}
}
