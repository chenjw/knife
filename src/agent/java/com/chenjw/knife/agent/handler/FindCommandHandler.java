package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.constants.Constants;

public class FindCommandHandler implements CommandHandler {

	private Class<?> findClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private Class<?>[] findLikeClass(String className) {
		List<Class<?>> likeClass = new ArrayList<Class<?>>();
		for (Class<?> clazz : Agent.getAllLoadedClasses()) {
			if (clazz.getName().indexOf(className) != -1) {
				likeClass.add(clazz);
			}
		}
		return likeClass.toArray(new Class<?>[likeClass.size()]);
	}

	private boolean isNumeric(String str) {
		return StringUtils.isNumeric(str);
	}

	public void handle(String[] args) {
		try {
			Class<?> clazz = null;
			String className = args[0];
			if (isNumeric(className)) {
				Class<?>[] likeClazz = (Class<?>[]) Context
						.get(Constants.CLASS_LIST);
				clazz = likeClazz[Integer.parseInt(className)];
			} else {
				clazz = findClass(className);
				if (clazz == null) {
					Class<?>[] likeClazz = findLikeClass(className);
					if (likeClazz.length > 1) {
						Context.put(Constants.CLASS_LIST, likeClazz);
						int i = 0;
						for (Class<?> cc : likeClazz) {
							Agent.print(i + ". " + cc.getName());
							i++;
						}
						Agent.print("find " + i + " classes like " + className
								+ ", please choose one typing like 'find 0'!");
						return;
					} else if (likeClazz.length == 1) {
						clazz = likeClazz[0];
					}
				}
				if (clazz == null) {
					Agent.print("not found!");
					return;
				}
			}
			// //
			Object[] objs = NativeHelper.findInstancesByClass(clazz);
			Context.put(Constants.LIST, objs);
			int i = 0;
			for (Object obj : objs) {
				Agent.print(i + ". " + obj);
				i++;
			}
			Agent.print("find " + i + " instances of " + clazz.getName());
		} catch (Exception e) {
			Agent.print(e.getClass().getName() + ":" + e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "find";
	}
}
