package com.chenjw.knife.agent.handler;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.service.ContextManager;
import com.chenjw.knife.agent.service.ObjectRecordManager;
import com.chenjw.knife.agent.util.NativeHelper;
import com.chenjw.knife.agent.util.StringHelper;
import com.chenjw.knife.agent.util.ToStringHelper;

public class LsCommandHandler implements CommandHandler {

	private void lsField(Args args) {
		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
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
				Agent.println("[static-field] "
						+ entry.getKey().getName()
						+ " = "
						+ ObjectRecordManager.getInstance().toId(
								entry.getValue())
						+ toString(args, entry.getValue()));
			}
		}
		if (obj != null) {
			Map<Field, Object> fieldMap = NativeHelper.getFieldValues(obj);
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				Agent.println("[field] "
						+ entry.getKey().getName()
						+ " = "
						+ ObjectRecordManager.getInstance().toId(
								entry.getValue())
						+ toString(args, entry.getValue()));
			}
		}
		Agent.println("finished!");
	}

	private void lsMethod(Args args) {

		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
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
		ContextManager.getInstance().put(Constants.METHOD_LIST,
				list.toArray(new Method[list.size()]));
		Agent.println("finished!");
	}

	private void lsConstruct(Args args) {
		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
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
		List<Constructor<?>> list = new ArrayList<Constructor<?>>();
		int i = 0;
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			Agent.println(i + ". [constructor] " + clazz.getSimpleName() + "("
					+ getParamClassNames(constructor.getParameterTypes()) + ")");
			list.add(constructor);
			i++;
		}
		ContextManager.getInstance().put(Constants.CONSTRUCTOR_LIST,
				list.toArray(new Constructor[list.size()]));
		Agent.println("finished!");

	}

	private void lsClass(Args args) {
		Object obj = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
		} else if (StringHelper.isNumeric(className)) {
			obj = ObjectRecordManager.getInstance().get(
					Integer.parseInt(className));
		}
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		Agent.println(" " + ObjectRecordManager.getInstance().toId(obj)
				+ toString(args, obj));
		Agent.println("finished!");
	}

	private void lsArray(Args args) {
		Object obj = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
		} else if (StringHelper.isNumeric(className)) {
			obj = ObjectRecordManager.getInstance().get(
					Integer.parseInt(className));
		}
		if (obj == null) {
			Agent.println("not found!");
			return;
		}
		if (obj.getClass().isArray()) {

			for (int i = 0; i < Array.getLength(obj); i++) {
				Object aObj = Array.get(obj, i);
				Agent.println(i + ". "
						+ ObjectRecordManager.getInstance().toId(aObj)
						+ toString(args, aObj));
			}
			Agent.println("finished!");
		} else {
			Agent.println("not array!");
			return;
		}

	}

	private static String toString(Args args, Object obj) {
		String rr = null;
		if (args.option("-d") != null) {
			rr = ToStringHelper.toDetailString(obj);
		} else {
			rr = ToStringHelper.toString(obj);
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
		} else if (args.option("-c") != null) {
			lsConstruct(args);
		} else {
			lsClass(args);
		}
	}

	public static String getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = Helper.makeClassName(classes[i]);
		}
		return StringHelper.join(classNames, ",");
	}

	@Override
	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("ls");
		argDef.setDef("[-f] [-m] [-c] [-a] [-d] [<classname>]");
		argDef.setDesc("list fields and methods of the target object.");
		argDef.addOptionDesc(
				"classname",
				"set <classname> to find static fields or methods , if <classname> not set , will apply to target object.");

		argDef.addOptionDesc("-f", "list fields.");
		argDef.addOptionDesc("-m", "list methods.");
		argDef.addOptionDesc("-c", "list constructs.");
		argDef.addOptionDesc("-a", "list array component.");
		argDef.addOptionDesc("-d", "to detail string.");

	}
}
