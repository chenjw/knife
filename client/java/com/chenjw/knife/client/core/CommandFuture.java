package com.chenjw.knife.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;

public class CommandFuture {

	private static final Map<String, CommandFuture> FUTURES = new ConcurrentHashMap<String, CommandFuture>();

	private final String id;

	private final Command command;

	private final Lock lock = new ReentrantLock();

	private final Condition done = lock.newCondition();

	private List<ResultPart> parts = new ArrayList<ResultPart>();

	private volatile Result result = null;

	private volatile ResponseCallback callback;

	public CommandFuture(Command request, ResponseCallback callback) {

		this.command = request;
		this.id = request.getId();
		this.callback = callback;
		// System.out.println("future put " + id);
		FUTURES.put(id, this);

	}

	public ResultModel getResult() {
		if (!isDone()) {
			lock.lock();
			try {
				while (!isDone()) {
					done.await();
					if (isDone()) {
						break;
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
			if (!isDone()) {
				throw new RuntimeException("time out");
			}
		}
		return currentResultModel();
	}

	private ResultModel currentResultModel() {
		ResultModel rm = new ResultModel();
		rm.setContent(result.getContent());
		rm.setErrorMessage(result.getErrorMessage());
		rm.setErrorTrace(result.getErrorTrace());
		rm.setRequestId(result.getRequestId());
		rm.setSuccess(result.isSuccess());
		rm.setParts(parts);
		return rm;
	}

	public boolean isDone() {
		return result != null;
	}

	public Command getCommand() {
		return command;
	}

	public static CommandFuture getFuture(long id) {
		return FUTURES.get(id);
	}

	public static void resultReceived(Result result) {

		CommandFuture future = FUTURES.remove(result.getRequestId());

		if (future != null) {
			// System.out.println(result.getRequestId() + " return "
			// + result.getContent());
			future.doResultReceived(result);
		} else {
			System.out.println("future not found,id=" + result.getRequestId());
		}
	}

	public static void partReceived(ResultPart part) {
		CommandFuture future = FUTURES.get(part.getRequestId());
		if (future != null) {
			future.doPartReceived(part);
		}

	}

	private void doPartReceived(ResultPart part) {
		lock.lock();
		try {
			parts.add(part);
		} finally {
			lock.unlock();
		}

		if (callback != null) {
			callback.onPart(part);
		}
	}

	private void doResultReceived(Result r) {
		lock.lock();
		try {
			this.result = r;
			if (done != null) {
				done.signal();
			}
		} finally {
			lock.unlock();
		}
		if (callback != null) {
			callback.done(currentResultModel());
		}
	}

}