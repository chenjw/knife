package com.chenjw.knife.client.formater;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.chenjw.knife.client.core.CommandListener;
import com.chenjw.knife.client.core.Completable;

import com.chenjw.knife.core.Printer;

public class FormaterManager {
	private Printer printer;
	private CommandListener commandListener;
	private Completable completeHandler;

	public FormaterManager(Printer printer, CommandListener commandListener,
			Completable completeHandler) {
		this.printer = printer;
		this.commandListener = commandListener;
		this.completeHandler = completeHandler;

		for (TypePrintFormater<?> service : ServiceLoader
				.load(TypePrintFormater.class)) {
			add(service);
		}
	}

	@SuppressWarnings("rawtypes")
	private Map<Class, TypePrintFormater> formaters = new ConcurrentHashMap<Class, TypePrintFormater>();

	@SuppressWarnings("rawtypes")
	public TypePrintFormater get(Class<?> clazz) {
		return formaters.get(clazz);
	}

	@SuppressWarnings("rawtypes")
	private void add(TypePrintFormater formater) {
		Class type = formater.getType();
		if (formater instanceof BasePrintFormater) {
			BasePrintFormater bf = (BasePrintFormater) formater;
			bf.setCompleteHandler(completeHandler);
			bf.setCommandListener(commandListener);
			
			bf.setPrinter(printer);
		}

		if (type != null) {
			if (formaters.put(type, formater) != null) {
				throw new RuntimeException(formater + " registed!");
			}

		} else {
			throw new RuntimeException(formater + "'s type not found!");
		}
	}
}
