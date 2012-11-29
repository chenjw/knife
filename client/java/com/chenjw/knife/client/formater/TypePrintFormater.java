package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;

public interface TypePrintFormater<T> {
	public Class<T> getType();

	public void print(Level level, Printer printer, T obj);
}
