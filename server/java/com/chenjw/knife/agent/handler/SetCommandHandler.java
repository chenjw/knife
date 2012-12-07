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
			obj = ContextManager.getInstance().get(Constants.THIS);
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
			obj = ContextManager.getInstance().get(Constants.THIS);
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
		argDef.setCommandName("set");
		argDef.setDef("[-s] <fieldname> <new-value>");
		argDef.setDesc("更改目标对象的某个属性值。");
		argDef.addOptionDesc("fieldname",
				"输入 'package.TestClass.field1' 来设置某个类的静态变量。或者输入 'field1' 表示目标对象的某个静态或非静态方法。");
		argDef.addOptionDesc("new-value",
				"用来表示新设置的值，可以使用json格式，或者直接输入 '@1' 格式的对象id来指定要设置的对象。");
		argDef.addOptionDesc("-s", "强制设置静态属性， 避免误解。");

	}

}
