package com.chenjw.knife.agent.utils;

import com.chenjw.knife.core.model.Result;

public class ResultHelper {
	public static Result newErrorResult(String errorMsg) {
		Result r = new Result();
		r.setSuccess(false);
		r.setErrorMessage(errorMsg);
		return r;
	}

	public static Result newErrorResult(String errorMsg, Throwable t) {
		Result r = new Result();
		r.setSuccess(false);
		r.setErrorMessage(errorMsg);
		r.setErrorTrace(ToStringHelper.toExceptionTraceString(t));
		return r;
	}

	public static Result newStringResult(String msg) {
		Result r = new Result();
		r.setSuccess(true);
		r.setContent(msg);
		return r;
	}

	public static Result newResult(Object obj) {
		Result r = new Result();
		r.setSuccess(true);
		r.setContent(obj);
		return r;
	}
}
