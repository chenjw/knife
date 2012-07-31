package com.chenjw.knife.agent.handler.log;

public interface ProfileStrategy {
	public boolean isStart(Object thisObject, String className,
			String methodName, Object[] arguments);

	public boolean isReturnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result);

	public void afterReturnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result);

	public boolean isExceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e);

	public void afterExceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e);

	public boolean isTrace(Class<?> clazz, String methodName);

	public boolean isEnter(Object thisObject, String className,
			String methodName, Object[] arguments);

	public boolean isLeave(Object thisObject, String className,
			String methodName, Object[] arguments, Object result);

	public ProfilerHandler getListener();

	public void setListener(ProfilerHandler listener);
}
