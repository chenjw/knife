package com.chenjw.knife.agent;

public interface CommandHandler {
	public String getName();

	public void handle(String[] args);
}
