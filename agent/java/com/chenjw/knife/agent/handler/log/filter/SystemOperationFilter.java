package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.utils.TimingHelper;

public class SystemOperationFilter implements Filter {
	private volatile boolean isOnSystemOperation = false;

	private void enterSystemOperation() {
		isOnSystemOperation = true;
		TimingHelper.stop();
	}

	private void leaveSystemOperation() {
		isOnSystemOperation = false;
		TimingHelper.resume();
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
