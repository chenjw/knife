package com.chenjw.knife.agent.core;

import java.util.Map;

import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.model.Command;

public interface CommandDispatcher {
	public void dispatch(Command command);

	public Map<String, ArgDef> getArgDefMap();

	public void setDescLanguage(String language);
}
