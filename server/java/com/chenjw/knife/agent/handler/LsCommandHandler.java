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
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.service.ObjectRecordService;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.model.ArrayInfo;
import com.chenjw.knife.core.model.ClassConstructorInfo;
import com.chenjw.knife.core.model.ClassFieldInfo;
import com.chenjw.knife.core.model.ClassMethodInfo;
import com.chenjw.knife.core.model.ConstructorInfo;
import com.chenjw.knife.core.model.ExceptionInfo;
import com.chenjw.knife.core.model.FieldInfo;
import com.chenjw.knife.core.model.MethodInfo;
import com.chenjw.knife.core.model.ObjectInfo;
import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.StringHelper;

public class LsCommandHandler implements CommandHandler {

	public class ClassOrObject {
		private Object obj = null;
		private Class<?> clazz = null;

		public boolean isObject() {
			return obj != null;
		}

		public boolean isClazz() {
			return obj == null && clazz != null;
		}

		public boolean isNotFound() {
			return obj == null && clazz == null;
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			if (obj != null) {
				this.obj = obj;
				this.clazz = obj.getClass();
			}
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}
	}

	private ClassOrObject getTarget(Args args) {
		ClassOrObject target = new ClassOrObject();
		String className = args.arg("classname");
		if (StringHelper.isBlank(className)) {
			target.setObj(ContextService.getInstance().get(Constants.THIS));

		} else if (StringHelper.isNumeric(className)) {
			target.setObj(ObjectRecordService.getInstance().get(
					Integer.parseInt(className)));

		} else {
			target.setClazz(Helper.findClass(className));
		}
		return target;
	}

	private void lsField(Args args) {
		ClassOrObject target = getTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}
		List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
		if (target.getClazz() != null) {
			Map<Field, Object> fieldMap = NativeHelper
					.getStaticFieldValues(target.getClazz());
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				FieldInfo info = new FieldInfo();
				info.setStatic(true);
				info.setName(entry.getKey().getName());
				if (entry.getValue() != null) {
					ObjectInfo fValue = new ObjectInfo();
					fValue.setObjectId(ObjectRecordService.getInstance().toId(
							entry.getValue()));
					fValue.setValueString(toString(args, entry.getValue()));
					info.setValue(fValue);
				}
				fieldInfos.add(info);
			}
		}
		if (target.getObj() != null) {
			Map<Field, Object> fieldMap = NativeHelper.getFieldValues(target
					.getObj());
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				FieldInfo info = new FieldInfo();
				info.setStatic(false);
				info.setName(entry.getKey().getName());
				if (entry.getValue() != null) {
					ObjectInfo fValue = new ObjectInfo();
					fValue.setObjectId(ObjectRecordService.getInstance().toId(
							entry.getValue()));
					fValue.setValueString(toString(args, entry.getValue()));
					info.setValue(fValue);
				}
				fieldInfos.add(info);
			}
		}
		ClassFieldInfo info = new ClassFieldInfo();
		info.setFields(fieldInfos.toArray(new FieldInfo[fieldInfos.size()]));
		Agent.sendResult(ResultHelper.newResult(info));
	}

	private void lsMethod(Args args) {
		ClassOrObject target = getTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}
		List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
		List<Method> list = new ArrayList<Method>();

		if (target.getClazz() != null) {
			for (Method method : ReflectHelper.getMethods(target.getClazz())) {
				if (Modifier.isStatic(method.getModifiers())) {
					MethodInfo methodInfo = new MethodInfo();
					methodInfo.setStatic(true);
					methodInfo.setName(method.getName());
					methodInfo.setParamClassNames(getParamClassNames(method
							.getParameterTypes()));
					methodInfos.add(methodInfo);
					list.add(method);

				}
			}
		}
		if (target.getObj() != null) {
			for (Method method : ReflectHelper.getMethods(target.getClazz())) {
				if (!Modifier.isStatic(method.getModifiers())) {
					MethodInfo methodInfo = new MethodInfo();
					methodInfo.setStatic(false);
					methodInfo.setName(method.getName());
					methodInfo.setParamClassNames(getParamClassNames(method
							.getParameterTypes()));
					methodInfos.add(methodInfo);
					list.add(method);

				}
			}
		}
		ContextService.getInstance().put(Constants.METHOD_LIST,
				list.toArray(new Method[list.size()]));
		ClassMethodInfo info = new ClassMethodInfo();
		info.setMethods(methodInfos.toArray(new MethodInfo[methodInfos.size()]));
		Agent.sendResult(ResultHelper.newResult(info));
	}

	private void lsConstruct(Args args) {
		ClassOrObject target = getTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}
		List<ConstructorInfo> constructorInfos = new ArrayList<ConstructorInfo>();
		List<Constructor<?>> list = new ArrayList<Constructor<?>>();
		if (target.getClazz() != null) {
			Constructor<?>[] constructors = target.getClazz()
					.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				ConstructorInfo constructorInfo = new ConstructorInfo();
				constructorInfo
						.setParamClassNames(getParamClassNames(constructor
								.getParameterTypes()));
				constructorInfos.add(constructorInfo);
				list.add(constructor);
			}
		}
		ContextService.getInstance().put(Constants.CONSTRUCTOR_LIST,
				list.toArray(new Constructor[list.size()]));
		ClassConstructorInfo info = new ClassConstructorInfo();
		info.setConstructors(constructorInfos
				.toArray(new ConstructorInfo[constructorInfos.size()]));
		info.setClassSimpleName(target.getClazz().getSimpleName());
		Agent.sendResult(ResultHelper.newResult(info));

	}

	private void lsClass(Args args) {
		ClassOrObject target = getTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}

		if ((target.getObj() instanceof Throwable)) {
			ExceptionInfo info = new ExceptionInfo();
			info.setObjectId(ObjectRecordService.getInstance().toId(
					target.getObj()));
			info.setTraceString(ToStringHelper
					.toExceptionTraceString((Throwable) target.getObj()));
			Agent.sendResult(ResultHelper.newResult(info));
		} else {
			ObjectInfo info = new ObjectInfo();
			info.setObjectId(ObjectRecordService.getInstance().toId(
					target.getObj()));
			info.setValueString(toString(args, target.getObj()));
			Agent.sendResult(ResultHelper.newResult(info));
		}

	}

	@SuppressWarnings("unchecked")
	private void lsArray(Args args) {
		ClassOrObject target = getTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}

		if (target.getClazz().isArray()) {
			List<ObjectInfo> elements = new ArrayList<ObjectInfo>();
			for (int i = 0; i < Array.getLength(target.getObj()); i++) {
				Object aObj = Array.get(target.getObj(), i);
				ObjectInfo element = new ObjectInfo();
				element.setObjectId(ObjectRecordService.getInstance()
						.toId(aObj));
				element.setValueString(toString(args, aObj));
				elements.add(element);

			}
			ArrayInfo info = new ArrayInfo();
			info.setElements(elements.toArray(new ObjectInfo[elements.size()]));
			Agent.sendResult(ResultHelper.newResult(info));
		} else if (target.getObj() instanceof List) {
			List<ObjectInfo> elements = new ArrayList<ObjectInfo>();
			for (Object aObj : (List<Object>) target.getObj()) {
				ObjectInfo element = new ObjectInfo();
				element.setObjectId(ObjectRecordService.getInstance()
						.toId(aObj));
				element.setValueString(toString(args, aObj));
				elements.add(element);
			}
			ArrayInfo info = new ArrayInfo();
			info.setElements(elements.toArray(new ObjectInfo[elements.size()]));
			Agent.sendResult(ResultHelper.newResult(info));
		} else {
			Agent.sendResult(ResultHelper.newErrorResult("not array!"));
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

	public static String[] getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = Helper.makeClassName(classes[i]);
		}
		return classNames;
	}

	@Override
	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("ls");
		argDef.setDef("[-f] [-m] [-c] [-a] [-d] [<classname>]");
		argDef.setDesc("列出目标类或目标对象的属性、方法、构造函数等信息。");
		argDef.addOptionDesc("classname",
				"如果设置了 <classname> 就会列出该类的信息，如果 <classname> 没有设置 ， 就会列出目标对象的信息。");

		argDef.addOptionDesc("-f", "列出所有属性。");
		argDef.addOptionDesc("-m", "列出所有方法。");
		argDef.addOptionDesc("-c", "列出所有构造函数。");
		argDef.addOptionDesc("-a", "如果目标是数组或列表，列出所有元素。");
		argDef.addOptionDesc("-d", "显示目标的详细信息。");

	}

}
