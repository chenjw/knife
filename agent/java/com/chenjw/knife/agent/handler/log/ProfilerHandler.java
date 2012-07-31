package com.chenjw.knife.agent.handler.log;

import com.chenjw.knife.agent.handler.log.event.Event;

public interface ProfilerHandler {

	public void onEvent(Event event, ProfilerCallback callback)
			throws Exception;
}
