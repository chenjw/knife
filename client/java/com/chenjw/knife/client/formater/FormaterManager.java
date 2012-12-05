package com.chenjw.knife.client.formater;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.chenjw.knife.client.core.Completable;

public class FormaterManager {
	private Completable completeHandler;
	public FormaterManager(Completable completeHandler){
		this.completeHandler=completeHandler;
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
		formater.setCompleteHandler(completeHandler);
		if (type != null) {
			if (formaters.put(type, formater) != null) {
				throw new RuntimeException(formater + " registed!");
			}

		} else {
			throw new RuntimeException(formater + "'s type not found!");
		}
	}
	



}
