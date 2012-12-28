package com.chenjw.knife.client.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;
import com.chenjw.knife.testgt.CommandFuture;
import com.chenjw.knife.testgt.ResponseCallback;

public abstract class DefaultCommandSender implements CommandService,
		CommandSender {
	// 待发送队列
	private BlockingQueue<CommandFuture> sendWaitingQueue = new LinkedBlockingQueue<CommandFuture>();

	public final ResultModel sendSyncCommand(Command command) {
		CommandFuture future = new CommandFuture(command, null);
		sendWaitingQueue.add(future);
		return future.getResult();
	}

	public final void sendCommand(Command command,
			ResponseCallback resulthandler) {
		CommandFuture future = new CommandFuture(command, resulthandler);
		sendWaitingQueue.add(future);
	}

	@Override
	public final Command waitCommand() throws Exception {
		return (Command) sendWaitingQueue.take().getCommand();
	}

	@Override
	public final void handleResult(Result r) {
		CommandFuture.resultReceived(r);
	}

	@Override
	public final void handlePart(ResultPart r) {
		CommandFuture.partReceived(r);
	}

}
