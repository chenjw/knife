package com.chenjw.knife.agent.core;

import java.util.Map;

import com.chenjw.knife.core.args.ArgDef;

public interface CommandDispatcher {

	public Map<String, ArgDef> getArgDefMap();

	public void setDescLanguage(String language);
}
