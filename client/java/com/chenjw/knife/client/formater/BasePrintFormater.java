package com.chenjw.knife.client.formater;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.chenjw.knife.client.core.CommandListener;
import com.chenjw.knife.client.core.Completable;

public abstract class BasePrintFormater<T> extends GrepPrintFormater implements
		TypePrintFormater<T> {

	protected CommandListener commandListener;
	protected Completable completeHandler;

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		Type genType = getClass().getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return null;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (params == null || params.length <= 0) {
			return null;
		}
		return (Class<T>) params[0];
	}

	abstract protected void print(T obj);

	@Override
	public final void printObject(T obj) {
		try {
			print(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setCompleteHandler(Completable completeHandler) {
		this.completeHandler = completeHandler;
	}

	public void setCommandListener(CommandListener commandListener) {
		this.commandListener = commandListener;
	}

}
