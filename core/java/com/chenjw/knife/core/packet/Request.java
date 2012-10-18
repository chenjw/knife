package com.chenjw.knife.core.packet;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = -2149723010201821906L;
	private long id;
	private Object[] context;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Object[] getContext() {
		return context;
	}

	public void setContext(Object[] context) {
		this.context = context;
	}

}