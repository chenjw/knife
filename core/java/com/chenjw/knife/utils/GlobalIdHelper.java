package com.chenjw.knife.utils;

import java.util.concurrent.atomic.AtomicLong;

public class GlobalIdHelper {
	private static final AtomicLong NEXT_ID = new AtomicLong(0);

	public static String getGlobalId() {
		return String.valueOf(NEXT_ID.getAndIncrement());
	}
}
