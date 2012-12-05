package com.chenjw.knife.client.formater;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.Printer.Level;

public abstract class BasePrintFormater<T> extends GrepPrintFormater implements
		TypePrintFormater<T> {

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
	public final void print(Level level, Printer printer, T obj) {
		this.level = level;
		this.printer = printer;
		try{
			print(obj);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
