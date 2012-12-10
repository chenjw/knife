package com.chenjw.knife.agent.core;

public interface Lifecycle {

	public abstract void init();

	public abstract void clear();

	public abstract void close();
}
