package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;

/**
 * 针对trace和invoke命令这里设计了一个过滤器链模型
 * <p>字节码增强负责让目标类发出各种事件</p>
 * <p>而过滤器链则用来处理这些事件</p>
 * <p>对于事件处理的各种逻辑就可以通过扩展Filter接口来实现</p>
 * 
 * @author chenjw
 *
 */
public interface Filter {
	public void doFilter(Event event, FilterChain chain) throws Exception;
}
