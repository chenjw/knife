package com.chenjw.knife.agent.handler.log;

import com.chenjw.knife.agent.handler.log.event.Event;

public interface ProfilerListener {

	public void onEvent(Event event) throws Exception;
}
