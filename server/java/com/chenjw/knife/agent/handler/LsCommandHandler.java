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

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.formater.PreparedTableFormater;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.utils.StringHelper;

public class LsCommandHandler implements CommandHandler {

	private void lsField(Args args) {
		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			clazz = obj.getClass();
		} else if (StringHelper.isNumeric(className)) {
			obj = ObjectRecordManager.getInstance().get(
					Integer.parseInt(className));
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(className);
			if (clazz == null) {
				Agent.info("not found!");
				return;
			}
		}

		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("type", "field-name", "obj-id", "value");
		if (clazz != null) {
			Map<Field, Object> fieldMap = NativeHelper
					.getStaticFieldValues(clazz);
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				table.addLine("[static-field]", entry.getKey().getName(),
						ObjectRecordManager.getInstance()
								.toId(entry.getValue()),
						toString(args, entry.getValue()));
			}
		}
		if (obj != null) {
			Map<Field, Object> fieldMap = NativeHelper.getFieldValues(obj);
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				table.addLine("[field]", entry.getKey().getName(),
						ObjectRecordManager.getInstance()
								.toId(entry.getValue()),
						toString(args, entry.getValue()));
			}
		}
		table.print();
		Agent.info("finished!");
	}

	private void lsMethod(Args args) {

		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			clazz = obj.getClass();
		} else if (StringHelper.isNumeric(className)) {
			obj = ObjectRecordManager.getInstance().get(
					Integer.parseInt(className));
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(className);
			if (clazz == null) {
				Agent.info("not found!");
				return;
			}
		}
		List<Method> list = new ArrayList<Method>();
		int i = 0;
		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("idx", "type", "method");
		if (clazz != null) {
			Method[] methods = clazz.getMethods();

			for (Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())) {
					table.addLine(
							String.valueOf(i),
							"[static-method]",
							method.getName()
									+ "("
									+ getParamClassNames(method
											.getParameterTypes()) + ")");
					list.add(method);
					i++;
				}
			}
		}
		if (obj != null) {
			Method[] methods = obj.getClass().getMethods();
			for (Method method : methods) {
				if (!Modifier.isStatic(method.getModifiers())) {
					table.addLine(
							String.valueOf(i),
							"[method]",
							method.getName()
									+ "("
									+ getParamClassNames(method
											.getParameterTypes()) + ")");
					list.add(method);
					i++;
				}
			}
		}
		table.print();
		ContextManager.getInstance().put(Constants.METHOD_LIST,
				list.toArray(new Method[list.size()]));
		Agent.info("finished!");
	}

	private void lsConstruct(Args args) {
		Object obj = null;
		Class<?> clazz = null;
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			obj = ContextManager.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			clazz = obj.getClass();
		} else if (StringHelper.isNumeric(className)) {
			obj = ObjectRecordManager.getInstance().get(
					Integer.parseInt(className));
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			clazz = obj.getClass();
		} else {
			clazz = Helper.findClass(className);
			if (clazz == null) {
				Agent.info("not found!");
				return;
			}
		}
		List<Constructor<?>> list = new ArrayList<Constructor<?>>();
		int i = 0;
		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("idx", "type", "method");
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			table.addLine(
					String.valueOf(i),
					"[constructor]",
					clazz.getSimpleName()
							+ "("
							+ getParamClassNames(constructor
									.getParameterTypes()) + ")");
			list.add(constructor);
			i++;
		}
		table.print();
		ContextManager.getInstance().put(Constants.CONSTRUCTOR_LIST,
				list.toArray(new Constructor[list.size()]));
		Agent.info("finished!");

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
			Agent.info("not found!");
			return;
		}
		if ((obj instanceof Throwable) && (args.option("-d") != null)) {
			Agent.info(" " + ObjectRecordManager.getInstance().toId(obj));
			Agent.print((Throwable) obj);
		} else {
			Agent.info(" " + ObjectRecordManager.getInstance().toId(obj)
					+ toString(args, obj));

		}
		Agent.info("finished!");

	}

	@SuppressWarnings("unchecked")
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
			Agent.info("not found!");
			return;
		}
		if (obj.getClass().isArray()) {
			PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
					Agent.printer, args.getGrep());

			table.setTitle("idx", "obj-id", "element");
			for (int i = 0; i < Array.getLength(obj); i++) {
				Object aObj = Array.get(obj, i);
				table.addLine(String.valueOf(i), ObjectRecordManager
						.getInstance().toId(aObj), toString(args, aObj));
			}
			table.print();
			Agent.info("finished!");
		} else if (obj instanceof List) {
			PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
					Agent.printer, args.getGrep());

			table.setTitle("idx", "obj-id", "element");
			int i = 0;
			for (Object aObj : (List<Object>) obj) {
				table.addLine(String.valueOf(i), ObjectRecordManager
						.getInstance().toId(aObj), toString(args, aObj));
				i++;
			}
			table.print();
			Agent.info("finished!");
		} else {
			Agent.info("not array!");
		}

	}

	private static String toString(Args args, Object obj) {
		if (obj == null) {
			return "null";
		}
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
