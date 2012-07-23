package com.chenjw.knife.agent.handler.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvokeRecord {
	private static class Record {
		private List<Object> list = new ArrayList<Object>();
		private Map<Object, Integer> map = new HashMap<Object, Integer>();

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

	private static ThreadLocal<Record> record = new ThreadLocal<Record>();

	private static Record getRecord() {
		Record r = record.get();
		if (r == null) {
			r = new Record();
			record.set(r);
		}
		return r;
	}

	public static int record(Object obj) {
		Record r = getRecord();
		return r.add(obj);
	}

	public static String toId(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return "@" + InvokeRecord.record(obj) + " ";
		}
	}

	public static Object get(int num) {

		Record r = getRecord();
		return r.get(num);
	}

	public static void clear() {
		record.remove();
	}
}
