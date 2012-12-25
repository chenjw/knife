/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chenjw.knife.testgt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.chenjw.knife.core.model.Request;
import com.chenjw.knife.core.model.Response;

/**
 * DefaultFuture.
 * 
 * @author qian.lei
 * @author chao.liuc
 */
public class DefaultFuture {
	public static final int DEFAULT_TIMEOUT = 1000;

	private static final Map<String, DefaultFuture> FUTURES = new ConcurrentHashMap<String, DefaultFuture>();

	// invoke id.
	private final String id;

	private final Request request;

	private final int timeout;

	private final Lock lock = new ReentrantLock();

	private final Condition done = lock.newCondition();

	private final long start = System.currentTimeMillis();

	private volatile long sent;

	private volatile Response response;

	private volatile ResponseCallback callback;

	public DefaultFuture(Request request, int timeout) {

		this.request = request;
		this.id = request.getId();
		this.timeout = DEFAULT_TIMEOUT;
		// put into waiting map.
		FUTURES.put(id, this);

	}

	public Object get() {
		return get(timeout);
	}

	public Object get(int timeout) {
		if (timeout <= 0) {
			timeout = DEFAULT_TIMEOUT;
		}
		if (!isDone()) {
			long start = System.currentTimeMillis();
			lock.lock();
			try {
				while (!isDone()) {
					done.await(timeout, TimeUnit.MILLISECONDS);
					if (isDone()
							|| System.currentTimeMillis() - start > timeout) {
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
		return this.response;
	}

	public void cancel() {

		FUTURES.remove(id);

	}

	public boolean isDone() {
		return response != null;
	}

	public void setCallback(ResponseCallback callback) {
		if (isDone()) {
			invokeCallback(callback);
		} else {
			boolean isdone = false;
			lock.lock();
			try {
				if (!isDone()) {
					this.callback = callback;
				} else {
					isdone = true;
				}
			} finally {
				lock.unlock();
			}
			if (isdone) {
				invokeCallback(callback);
			}
		}
	}

	private void invokeCallback(ResponseCallback c) {
		ResponseCallback callbackCopy = c;
		if (callbackCopy == null) {
			throw new NullPointerException("callback cannot be null.");
		}
		c = null;
		Response res = response;
		if (res == null) {
			throw new IllegalStateException("response cannot be null. url:");
		}

	}

	private String getId() {
		return id;
	}

	private boolean isSent() {
		return sent > 0;
	}

	public Request getRequest() {
		return request;
	}

	private int getTimeout() {
		return timeout;
	}

	private long getStartTimestamp() {
		return start;
	}

	public static DefaultFuture getFuture(long id) {
		return FUTURES.get(id);
	}

	public static void sent(Request request) {
		DefaultFuture future = FUTURES.get(request.getId());
		if (future != null) {
			future.doSent();
		}
	}

	private void doSent() {
		sent = System.currentTimeMillis();
	}

	public static void received(Response response) {

		DefaultFuture future = FUTURES.remove(response.getId());
		if (future != null) {
			future.doReceived(response);
		}

	}

	private void doReceived(Response res) {
		lock.lock();
		try {
			response = res;
			if (done != null) {
				done.signal();
			}
		} finally {
			lock.unlock();
		}
		if (callback != null) {
			invokeCallback(callback);
		}
	}

	private static class RemotingInvocationTimeoutScan implements Runnable {

		public void run() {
			while (true) {
				try {
					for (DefaultFuture future : FUTURES.values()) {
						if (future == null || future.isDone()) {
							continue;
						}
						if (System.currentTimeMillis()
								- future.getStartTimestamp() > future
									.getTimeout()) {
							// create exception response.
							Response timeoutResponse = new Response();
							timeoutResponse.setId(future.getId());

							// handle response.
							DefaultFuture.received(timeoutResponse);
						}
					}
					Thread.sleep(30);
				} catch (Throwable e) {

				}
			}
		}
	}

	static {
		Thread th = new Thread(new RemotingInvocationTimeoutScan(),
				"DubboResponseTimeoutScanTimer");
		th.setDaemon(true);
		th.start();
	}

}