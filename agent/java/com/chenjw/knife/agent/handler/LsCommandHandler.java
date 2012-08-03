package com.chenjw.knife.agent.handler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.InvokeRecord;
import com.chenjw.knife.agent.handler.log.ParseHelper;

public class LsCommandHandler implements CommandHandler {

	private void lsField(Args args) {
		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringUtils.isBlank(className)) {
			obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.println("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(className);
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
						+ toString(args, entry.getValue()));
			}
		}
		if (obj != null) {
			Map<Field, Object> fieldMap = NativeHelper.getFieldValues(obj);
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				Agent.println("[field] " + entry.getKey().getName() + " = "
						+ InvokeRecord.toId(entry.getValue())
						+ toString(args, entry.getValue()));
			}
		}
		Agent.println("finished!");
	}

	private void lsMethod(Args args) {

		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringUtils.isBlank(className)) {
			obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.println("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(className);
			if (clazz == null) {
				Agent.println("not found!");
				return;
			}
		}
		List<Method> list = new ArrayList<Method>();
		int i = 0;
		if (clazz != null) {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())) {
					Agent.println(i + ". [static-method] " + method.getName()
							+ "("
							+ getParamClassNames(method.getParameterTypes())
							+ ")");
					list.add(method);
					i++;
				}
			}
		}
		if (obj != null) {
			Method[] methods = obj.getClass().getMethods();
			for (Method method : methods) {
				if (!Modifier.isStatic(method.getModifiers())) {
					Agent.println(i + ". [method] " + method.getName() + "("
							+ getParamClassNames(method.getParameterTypes())
							+ ")");
					list.add(method);
					i++;
				}
			}
		}
		Context.put(Constants.METHOD_LIST,
				list.toArray(new Method[list.size()]));
		Agent.println("finished!");

	}

	private void lsClass(Args args) {
		Object obj = null;
		String className = args.arg("classname");
		if (StringUtils.isBlank(className)) {
			obj = Context.get(Constants.THIS);
		} else if (StringUtils.isNumeric(className)) {
			obj = InvokeRecord.get(Integer.parseInt(className));
		}
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		Agent.println(" " + InvokeRecord.toId(obj) + toString(args, obj));
		Agent.println("finished!");
	}

	private void lsArray(Args args) {
		Object obj = null;
		String className = args.arg("classname");
		if (StringUtils.isBlank(className)) {
			obj = Context.get(Constants.THIS);
		} else if (StringUtils.isNumeric(className)) {
			obj = InvokeRecord.get(Integer.parseInt(className));
		}
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		if (obj.getClass().isArray()) {

			for (int i = 0; i < Array.getLength(obj); i++) {
				Object aObj = Array.get(obj, i);
				Agent.println(i + ". " + InvokeRecord.toId(aObj)
						+ toString(args, aObj));
			}
			Agent.println("finished!");
		} else {
			Agent.println("not array!");
			return;
		}

	}

	private static String toClassName(Object obj) {
		if (obj == null) {
			return null;
		} else {
			return obj.getClass().getName();
		}
	}

	private static String toString(Args args, Object obj) {
		String rr = null;
		if (args.option("-j") != null) {
			rr = ParseHelper.toJsonString(obj);
		} else {
			rr = ParseHelper.toString(obj);
		}
		return rr;
	}

	public void handle(Args args, CommandDispatcher dispatcher) {
		if (args.option("-f") != null) {
			lsField(args);
		} else if (args.option("-m") != null) {
			lsMethod(args);
		} else if (args.option("-a") != null) {
			lsArray(args);
		} else {
			lsClass(args);
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
	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("ls");
		argDef.setDef("[-f] [-m] [-a] [-j] [<classname>]");
		argDef.setDesc("list fields and methods of the target object.");
		argDef.addOptionDesc(
				"classname",
				"set <classname> to find static fields or methods , if <classname> not set , will apply to target object.");

		argDef.addOptionDesc("-f", "list fields.");
		argDef.addOptionDesc("-m", "list methods.");
		argDef.addOptionDesc("-a", "list array component.");
		argDef.addOptionDesc("-j", "to json string.");

	}
}
