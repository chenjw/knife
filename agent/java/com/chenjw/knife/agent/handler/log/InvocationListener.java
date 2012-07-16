package com.chenjw.knife.agent.handler.log;

public interface InvocationListener {

	public void onStart(int dep, Object thisObject, String className,
			String methodName, Object[] arguments);

	public void onReturnEnd(int dep, Object thisObject, String className,
			String methodName, Object[] arguments, Object result);

	public void onExceptionEnd(int dep, Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e);
}
