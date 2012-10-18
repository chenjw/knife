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
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ParseHelper;
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
				Agent.info("class not found!");
				return;
			}
			field = NativeHelper.findStaticField(clazz, fieldName);

		} else if (args.arg("-s") != null) {
			obj = ContextManager.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			Class<?> clazz = obj.getClass();
			if (clazz == null) {
				Agent.info("class not found!");
				return;
			}
			field = NativeHelper.findStaticField(clazz, fieldName);
		} else {
			obj = ContextManager.getInstance().get(Constants.THIS);
			if (obj == null) {
				Agent.info("not found!");
				return;
			}
			field = NativeHelper.findField(obj, fieldName);
			if (field == null) {
				Class<?> clazz = obj.getClass();
				field = NativeHelper.findStaticField(clazz, fieldName);
			}
		}
		if (field == null) {
			Agent.info("field " + fieldName + " not found!");
			return;
		}
		Object newValue = ParseHelper.parseValue(value, field.getType());
		setFieldValue(obj, field, newValue);

		Agent.info("finished!");
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
		argDef.setCommandName("set");
		argDef.setDef("[-s] <fieldname> <new-value>");
		argDef.setDesc("set field value to target object.");
		argDef.addOptionDesc(
				"fieldname",
				"input 'package.TestClass.field1' means static field or 'field1' means both static and no-static field of target object.");
		argDef.addOptionDesc(
				"new-value",
				"an expretion which will transfer to object by json tool, or '@1' means direct the object by id.");
		argDef.addOptionDesc("-s",
				"force set to static field, to avoid misunderstanding");

	}

}
