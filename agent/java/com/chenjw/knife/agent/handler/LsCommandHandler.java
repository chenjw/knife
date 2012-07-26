package com.chenjw.knife.agent.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

	private void lsField(Args args) {
		Object obj = null;
		Class<?> clazz = null;
		String arg0 = args.arg(0);
		if (StringUtils.isBlank(arg0)) {
			obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.println("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(arg0);
			if (clazz == null) {
				Agent.println("not found!");
				return;
			}
		}
		if (clazz != null) {
			Map<Field, Object> fieldMap = NativeHelper
					.getStaticFieldValues(clazz);
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				Agent.println("[static-field] " + entry.getKey().getName()
						+ " = " + InvokeRecord.toId(entry.getValue())
						+ toString(entry.getValue()));
			}
		}
		if (obj != null) {
			Map<Field, Object> fieldMap = NativeHelper.getFieldValues(obj);
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				Agent.println("[field] " + entry.getKey().getName() + " = "
						+ InvokeRecord.toId(entry.getValue())
						+ toString(entry.getValue()));
			}
		}
		Agent.println("finished!");
	}

	private void lsMethod(Args args) {

		Object obj = null;
		Class<?> clazz = null;
		String arg0 = args.arg(0);
		if (StringUtils.isBlank(arg0)) {
			obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.println("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(arg0);
			if (clazz == null) {
				Agent.println("not found!");
				return;
			}
		}
		List<Method> list = new ArrayList<Method>();
		int i = 0;
		if (clazz != null) {
			Method[] methods = clazz.getMethods();
			int j = 0;
			for (Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())) {
					Agent.println(i + ". [static-method] " + method.getName()
							+ "("
							+ getParamClassNames(method.getParameterTypes())
							+ ")");
					list.add(method);
					i++;
					j++;
				}
			}
		}
		if (obj != null) {
			Method[] methods = obj.getClass().getMethods();
			int j = 0;
			for (Method method : methods) {
				if (!Modifier.isStatic(method.getModifiers())) {
					Agent.println(i + ". [method] " + method.getName() + "("
							+ getParamClassNames(method.getParameterTypes())
							+ ")");
					list.add(method);
					i++;
					j++;
				}
			}
		}
		Context.put(Constants.METHOD_LIST,
				list.toArray(new Method[list.size()]));
		Agent.println("finished!");

	}

	private void lsClass(Args args) {
		Object obj = null;
		String arg0 = args.arg(0);
		if (StringUtils.isBlank(arg0)) {
			obj = Context.get(Constants.THIS);
		} else if (StringUtils.isNumeric(arg0)) {
			obj = InvokeRecord.get(Integer.parseInt(arg0));
		}
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		Agent.println(toString(obj));
		Agent.println("finished!");
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
				lsField(args);
			} else if (args.arg("-m") != null) {
				lsMethod(args);
			} else {
				lsClass(args);
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
