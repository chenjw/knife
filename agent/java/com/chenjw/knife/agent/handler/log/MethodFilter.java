package com.chenjw.knife.agent.handler.log;

public interface MethodFilter {
	public boolean doFilter(String className, String methodName);
}
