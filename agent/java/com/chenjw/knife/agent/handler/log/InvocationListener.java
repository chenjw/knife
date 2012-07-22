package com.chenjw.knife.agent.handler.log;

public interface InvocationListener {

	public void onStart(Object thisObject, String className, String methodName,
			Object[] arguments);

	public void onReturnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result);

	public void onExceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e);
}
