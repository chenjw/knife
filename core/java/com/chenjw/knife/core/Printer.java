package com.chenjw.knife.core;

public abstract class Printer {
	public enum Level {
		INFO, DEBUG;
	}

	public abstract void info(String str);

	public abstract void debug(String str);

	public void print(Level level, String str) {
		if (level == Level.INFO) {
			info(str);
		} else if (level == Level.INFO) {
			debug(str);
		} else {
			info(str);
		}
	}
}
