package com.chenjw.knife.core.model;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = -2149723010201821906L;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}