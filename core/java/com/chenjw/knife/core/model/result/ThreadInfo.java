package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ThreadInfo implements Serializable {

	private static final long serialVersionUID = 8123426855803797220L;
	private String tid;
	private String cpu;
	private String name;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}