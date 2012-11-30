package com.chenjw.knife.client.formater;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class FormaterManager {
	@SuppressWarnings("rawtypes")
	private Map<Class, TypePrintFormater> formaters = new ConcurrentHashMap<Class, TypePrintFormater>();

	{

		for (TypePrintFormater<?> service : ServiceLoader
				.load(TypePrintFormater.class)) {
			add(service);
		}

		// add(new ArrayFormater());
		// add(new ClassConstructorFormater());
		// add(new ClassFieldFormater());
		// add(new ClassListFormater());
		// add(new ClassMethodFormater());
		// add(new ExceptionFormater());
		// add(new InstanceListFormater());
		// add(new MethodExceptionEndFormater());
		// add(new MethodReturnEndFormater());
		// add(new MethodStartFormater());
		// add(new ObjectFormater());
		// add(new ReferenceListFormater());
		// add(new TopReferenceCountFormater());
		// add(new TopThreadFormater());

	}

	@SuppressWarnings("rawtypes")
	public TypePrintFormater get(Class<?> clazz) {
		return formaters.get(clazz);
	}

	@SuppressWarnings("rawtypes")
	private void add(TypePrintFormater formater) {
		Class type = formater.getType();
		if (type != null) {
			if (formaters.put(type, formater) != null) {
				throw new RuntimeException(formater + " registed!");
			}

		} else {
			throw new RuntimeException(formater + "'s type not found!");
		}
	}

	public static void main(String[] args) {
		FormaterManager t = new FormaterManager();
		t.add(new ExceptionFormater());
	}

}
