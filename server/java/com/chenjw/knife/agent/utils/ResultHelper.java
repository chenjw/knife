package com.chenjw.knife.agent.utils;

import com.chenjw.knife.core.result.Result;

public class ResultHelper {
	public static Result<Object> newErrorResult(String errorMsg) {
		Result<Object> r = new Result<Object>();
		r.setSuccess(false);
		r.setErrorMessage(errorMsg);
		return r;
	}

	public static Result<Object> newStringResult(String msg) {
		Result<String> r = new Result<String>();
		r.setSuccess(true);
		r.setContent(msg);
		return r;
	}
}
