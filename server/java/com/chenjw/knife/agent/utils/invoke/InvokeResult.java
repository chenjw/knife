package com.chenjw.knife.agent.utils.invoke;

public class InvokeResult {
	private Object result;
	private Throwable e;

	public boolean isSuccess() {
		return e == null;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Throwable getE() {
		return e;
	}

	public void setE(Throwable e) {
		this.e = e;
	}

}
