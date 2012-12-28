package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class TopThreadInfo implements Serializable {

	private static final long serialVersionUID = -2538967520334519918L;
	private ThreadInfo[] threads;

	public ThreadInfo[] getThreads() {
		return threads;
	}

	public void setThreads(ThreadInfo[] threads) {
		this.threads = threads;
	}

}
