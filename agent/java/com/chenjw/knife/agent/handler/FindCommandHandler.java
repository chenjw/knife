package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.InvokeRecord;

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

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			Class<?> clazz = null;
			String className = args.arg(0);
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
							Agent.println(i + ". " + cc.getName());
							i++;
						}
						Agent.println("find " + i + " classes like '"
								+ className
								+ "', please choose one typing like 'find 0'!");
						return;
					} else if (likeClazz.length == 1) {
						clazz = likeClazz[0];
					}
				}
				if (clazz == null) {
					Agent.println("not found!");
					return;
				}
			}
			// //
			Object[] objs = NativeHelper.findInstancesByClass(clazz);
			int i = 0;
			for (Object obj : objs) {
				Agent.println(InvokeRecord.toId(obj) + obj);
				i++;
			}
			Agent.println("find " + i + " instances of " + clazz.getName());
		} catch (Exception e) {
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "find";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {

	}
}
