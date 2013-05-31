package com.chenjw.knife.agent.core;

/**
 * 服务生命周期，实现这个接口的服务，在框架执行的生命周期中，相应的方法会被调用到
 * 
 * @author chenjw
 *
 **/
public interface Lifecycle {

	public abstract void init();

	public abstract void clear();

	public abstract void close();
}
