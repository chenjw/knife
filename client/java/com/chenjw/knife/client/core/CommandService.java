package com.chenjw.knife.client.core;

import com.chenjw.knife.core.Command;
import com.chenjw.knife.core.result.Result;

public interface CommandService {

	public int waitVMIndex() throws Exception;

	public Command waitCommand() throws Exception;

	public void handleResult(Result result) throws Exception;

	public void handleText(String text) throws Exception;

	public void close() throws Exception;

}
