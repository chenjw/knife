package com.chenjw.knife.client.model;

import java.io.Serializable;

public class AttachRequest implements Serializable {

	private static final long serialVersionUID = -3420882033931905060L;
	private String pid;
	private int port;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
