package com.chenjw.knife.agent;

import com.chenjw.knife.agent.event.Event;

public interface ProfilerListener {

	public void onEvent(Event event) throws Exception;
}
