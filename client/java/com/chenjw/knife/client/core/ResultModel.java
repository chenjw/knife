package com.chenjw.knife.client.core;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;

public class ResultModel extends Result {

	private static final long serialVersionUID = -7094909020215207506L;
	private List<ResultPart> parts = new ArrayList<ResultPart>();

	public List<ResultPart> getParts() {
		return parts;
	}

	public void setParts(List<ResultPart> parts) {
		this.parts = parts;
	}

}
