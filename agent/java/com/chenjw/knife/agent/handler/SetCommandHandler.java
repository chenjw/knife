package com.chenjw.knife.agent.handler;

import java.lang.reflect.Field;
import java.util.Map;

import javassist.Modifier;

import org.apache.commons.lang.StringUtils;

import com.chenjw.bytecode.javassist.Helper;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.agent.handler.log.ParseHelper;

public class SetCommandHandler implements CommandHandler {

	private void setField(Args args) {
		String fieldName = args.arg(0);
		String value = args.arg(1);
		Object obj = null;
		Field field = null;

		if (fieldName.indexOf(".") != -1) {
			String className = StringUtils.substringBeforeLast(fieldName, ".");
			fieldName = StringUtils.substringAfterLast(fieldName, ".");
			Class<?> clazz = Helper.findClass(className);
			if (clazz == null) {
				Agent.println("class not found!");
				return;
			}
			field = NativeHelper.findStaticField(clazz, fieldName);

		} else if (args.arg("-s") != null) {
			obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.println("not found!");
				return;
			}
			Class<?> clazz = obj.getClass();
			if (clazz == null) {
				Agent.println("class not found!");
				return;
			}
			field = NativeHelper.findStaticField(clazz, fieldName);
		} else {
			obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.println("not found!");
				return;
			}
			field = NativeHelper.findField(obj, fieldName);
			if (field == null) {
				Class<?> clazz = obj.getClass();
				field = NativeHelper.findStaticField(clazz, fieldName);
			}
		}
		if (field == null) {
			Agent.println("field " + fieldName + " not found!");
			return;
		}
		Object newValue = ParseHelper.parseValue(value, field.getType());
		setFieldValue(obj, field, newValue);

		Agent.println("finished!");
	}

	public void setFieldValue(Object obj, Field field, Object newValue) {
		if (Modifier.isStatic(field.getModifiers())) {
			NativeHelper.setStaticFieldValue(field.getDeclaringClass(), field,
					newValue);
		} else {
			NativeHelper.setFieldValue(obj, field, newValue);
		}
	}

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			setField(args);
		} catch (Exception e) {
			e.printStackTrace();
			Agent.println(e.getClass().getName() + ":"
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {
		argDecls.put("-s", 0);
	}
}
