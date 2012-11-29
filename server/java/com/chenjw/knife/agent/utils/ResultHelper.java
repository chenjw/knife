package com.chenjw.knife.agent.utils;

import com.chenjw.knife.core.result.Result;

public class ResultHelper {
	public static Result<Object> newErrorResult(String errorMsg) {
		Result<Object> r = new Result<Object>();
		r.setSuccess(false);
		r.setErrorMessage(errorMsg);
		return r;
	}
	
	public static Result<Object> newErrorResult(String errorMsg,Throwable t) {
		Result<Object> r = new Result<Object>();
		r.setSuccess(false);
		r.setErrorMessage(errorMsg);
		r.setErrorTrace(ToStringHelper.toExceptionTraceString(t));
		return r;
	}

	public static Result<String> newStringResult(String msg) {
		Result<String> r = new Result<String>();
		r.setSuccess(true);
		r.setContent(msg);
		return r;
	}
}
