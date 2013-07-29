package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;

public interface CommandService {

	public Command waitCommand() throws Exception;

	public void handleResult(Result result);

	public void handlePart(ResultPart result);

	public void handleText(String text);

	public void close() throws Exception;

}
