package com.chenjw.knife.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于生成全局唯一的ID
 * 
 * @author chenjw
 * 
 */
public class GlobalIdHelper {
	private static final AtomicLong NEXT_ID = new AtomicLong(0);

	public static String getGlobalId() {
		return String.valueOf(NEXT_ID.getAndIncrement());
	}
}
