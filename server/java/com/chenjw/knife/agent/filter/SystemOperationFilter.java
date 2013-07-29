package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.event.Event;

/**
 * 用于过滤掉框架类的调用
 * 
 * @author chenjw
 *
 */
public class SystemOperationFilter implements Filter {
	private volatile boolean isOnSystemOperation = false;

	private void enterSystemOperation() {
		isOnSystemOperation = true;
	}

	private void leaveSystemOperation() {
		isOnSystemOperation = false;
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {

		if (isOnSystemOperation) {
			return;
		}
		try {
			enterSystemOperation();
			chain.doFilter(event);
		} finally {
			leaveSystemOperation();
		}
	}

}
