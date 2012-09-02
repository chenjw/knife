package com.chenjw.knife.agent.manager;

public interface Lifecycle {

	public abstract void init();

	public abstract void clear();

	public abstract void close();
}
