package com.chenjw.knife.agent.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.InvokeRecord;

public class LsCommandHandler implements CommandHandler {

	private void lsField() {
		Object obj = Context.get(Constants.THIS);
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		Map<Field, Object> fieldMap = NativeHelper.getFieldValues(obj);
		List<Object> list = new ArrayList<Object>();
		int i = 0;
		for (Entry<Field, Object> entry : fieldMap.entrySet()) {
			Agent.println(entry.getKey().getName() + "="
					+ InvokeRecord.toId(entry.getValue()) + entry.getValue());
			list.add(entry.getValue());
			i++;
		}
		Context.put(Constants.OBJECT_LIST,
				list.toArray(new Object[list.size()]));
		Agent.println("find " + i + " fields of " + obj);
	}

	private void lsMethod() {
		Object obj = Context.get(Constants.THIS);
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		int i = 0;
		Method[] methods = obj.getClass().getMethods();
		for (Method method : methods) {
			Agent.println(i + ". " + method.getName() + "("
					+ getParamClassNames(method.getParameterTypes()) + ")");
			i++;
		}
		Context.put(Constants.METHOD_LIST, methods);
		Agent.println("find " + i + " methods of " + obj);
	}

	private void lsClass() {
		Object obj = Context.get(Constants.THIS);
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		Agent.println(toString(obj));
	}

	private static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return JSON.toJSONString(obj);
		} catch (Throwable e) {
			return obj.toString();
		}
	}

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			if (args.arg("-f") != null) {
				lsField();
			} else if (args.arg("-m") != null) {
				lsMethod();
			} else {
				lsClass();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		}
	}

	public static String getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = Helper.makeClassName(classes[i]);
		}
		return StringUtils.join(classNames, ",");
	}

	@Override
	public String getName() {
		return "ls";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {
		argDecls.put("-f", 0);
		argDecls.put("-m", 0);
	}
}
