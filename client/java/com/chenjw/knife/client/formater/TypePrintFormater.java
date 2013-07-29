package com.chenjw.knife.client.formater;

public interface TypePrintFormater<T> {
	public Class<T> getType();

	public void printObject(T obj);

}
