package com.chenjw.knife.core;

public abstract class Printer {
	public enum Level {
		INFO, DEBUG;
	}

	
	public abstract void clear();
	
	public abstract int info(String str);

	public abstract int debug(String str);

	public int print(Level level, String str) {
		if (level == Level.INFO) {
			return info(str);
		} else if (level == Level.INFO) {
			return debug(str);
		} else {
			return info(str);
		}
	}
}
