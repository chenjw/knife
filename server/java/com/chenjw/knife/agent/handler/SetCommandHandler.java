package com.chenjw.knife.agent.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ParseHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.utils.StringHelper;

public class SetCommandHandler implements CommandHandler {

	private void setField(Args args) {
		String fieldName = args.arg("fieldname");
		String value = args.arg("new-value");
		Object obj = null;
		Field field = null;

		if (fieldName.indexOf(".") != -1) {
			String className = StringHelper.substringBeforeLast(fieldName, ".");
			fieldName = StringHelper.substringAfterLast(fieldName, ".");
			Class<?> clazz = Helper.findClass(className);
			if (clazz == null) {
				Agent.sendResult(ResultHelper
						.newErrorResult("class not found!"));
				return;
			}
			field = NativeHelper.findStaticField(clazz, fieldName);

		} else if (args.arg("-s") != null) {
			obj = ContextService.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.sendResult(ResultHelper.newErrorResult("not found!"));

				return;
			}
			Class<?> clazz = obj.getClass();
			if (clazz == null) {
				Agent.sendResult(ResultHelper
						.newErrorResult("class not found!"));

				return;
			}
			field = NativeHelper.findStaticField(clazz, fieldName);
		} else {
			obj = ContextService.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.sendResult(ResultHelper.newErrorResult(" not found!"));
				return;
			}
			field = NativeHelper.findField(obj, fieldName);
			if (field == null) {
				Class<?> clazz = obj.getClass();
				field = NativeHelper.findStaticField(clazz, fieldName);
			}
		}
		if (field == null) {
			Agent.sendResult(ResultHelper.newErrorResult("field " + fieldName
					+ " not found!"));
			return;
		}
		Object newValue = ParseHelper.parseValue(value, field.getType());
		setFieldValue(obj, field, newValue);
		Agent.sendResult(ResultHelper.newStringResult("finished!"));

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
		setField(args);
	}

	@Override
	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("set [-s] <fieldname> <new-value>");

	}

}
