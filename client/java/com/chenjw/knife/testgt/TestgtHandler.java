package com.chenjw.knife.testgt;

import com.chenjw.knife.client.core.CommandService;
import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;

public class TestgtHandler implements CommandService, TestgtService {

	@Override
	public void sendCommand(Command command, ResultHandler resulthandler) {

	}

	@Override
	public int waitVMIndex() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Command waitCommand() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleResult(Result result) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleText(String text) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

}
