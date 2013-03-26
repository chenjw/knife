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
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.agent.utils.CommandHelper;
import com.chenjw.knife.agent.utils.CommandHelper.ClassOrObject;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.result.ArrayInfo;
import com.chenjw.knife.core.model.result.ClassConstructorInfo;
import com.chenjw.knife.core.model.result.ClassFieldInfo;
import com.chenjw.knife.core.model.result.ClassMethodInfo;
import com.chenjw.knife.core.model.result.ConstructorInfo;
import com.chenjw.knife.core.model.result.ExceptionInfo;
import com.chenjw.knife.core.model.result.FieldInfo;
import com.chenjw.knife.core.model.result.MethodInfo;
import com.chenjw.knife.core.model.result.ObjectInfo;
import com.chenjw.knife.utils.ClassHelper;
import com.chenjw.knife.utils.ReflectHelper;

public class LsCommandHandler implements CommandHandler {


	private ClassOrObject findTarget(Args args) {
		String className = args.arg("classname");
		return CommandHelper.findTarget(className);
	}

	private void lsField(Args args) {
		ClassOrObject target = findTarget(args);
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
					fValue.setObjectId(ServiceRegistry.getService(
							ObjectHolderService.class).toId(entry.getValue()));
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
					fValue.setObjectId(ServiceRegistry.getService(
							ObjectHolderService.class).toId(entry.getValue()));
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
		ClassOrObject target = findTarget(args);
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
		ServiceRegistry.getService(ContextService.class).put(
				Constants.METHOD_LIST, list.toArray(new Method[list.size()]));
		ClassMethodInfo info = new ClassMethodInfo();
		info.setMethods(methodInfos.toArray(new MethodInfo[methodInfos.size()]));
		Agent.sendResult(ResultHelper.newResult(info));
	}

	private void lsConstruct(Args args) {
		ClassOrObject target = findTarget(args);
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
		ServiceRegistry.getService(ContextService.class).put(
				Constants.CONSTRUCTOR_LIST,
				list.toArray(new Constructor[list.size()]));
		ClassConstructorInfo info = new ClassConstructorInfo();
		info.setConstructors(constructorInfos
				.toArray(new ConstructorInfo[constructorInfos.size()]));
		info.setClassSimpleName(target.getClazz().getSimpleName());
		Agent.sendResult(ResultHelper.newResult(info));

	}

	private void lsClass(Args args) {
		ClassOrObject target = findTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}

		if ((target.getObj() instanceof Throwable)) {
			ExceptionInfo info = new ExceptionInfo();
			info.setObjectId(ServiceRegistry.getService(
					ObjectHolderService.class).toId(target.getObj()));
			info.setTraceString(ToStringHelper
					.toExceptionTraceString((Throwable) target.getObj()));
			Agent.sendResult(ResultHelper.newResult(info));
		} else {
			ObjectInfo info = new ObjectInfo();
			info.setObjectId(ServiceRegistry.getService(
					ObjectHolderService.class).toId(target.getObj()));
			info.setValueString(toString(args, target.getObj()));
			Agent.sendResult(ResultHelper.newResult(info));
		}

	}

	@SuppressWarnings("unchecked")
	private void lsArray(Args args) {
		ClassOrObject target = findTarget(args);
		if (target.isNotFound()) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}

		if (target.getClazz().isArray()) {
			List<ObjectInfo> elements = new ArrayList<ObjectInfo>();
			int maxNum = 0;
			int length = Array.getLength(target.getObj());
			Map<String, String> nOptions = args.option("-n");
			if (nOptions != null) {
				maxNum = Integer.parseInt(nOptions.get("num"));
				if (maxNum > length) {
					maxNum = length;
				}
			} else {
				maxNum = length;
			}
			for (int i = 0; i < maxNum; i++) {
				Object aObj = Array.get(target.getObj(), i);
				ObjectInfo element = new ObjectInfo();
				element.setObjectId(ServiceRegistry.getService(
						ObjectHolderService.class).toId(aObj));
				element.setValueString(toString(args, aObj));
				elements.add(element);

			}
			ArrayInfo info = new ArrayInfo();
			info.setElements(elements.toArray(new ObjectInfo[elements.size()]));
			Agent.sendResult(ResultHelper.newResult(info));
		} else if (target.getObj() instanceof List) {

			List<Object> list = (List<Object>) target.getObj();
			List<ObjectInfo> elements = new ArrayList<ObjectInfo>();
			int maxNum = 0;
			int length = list.size();
			Map<String, String> nOptions = args.option("-n");
			if (nOptions != null) {
				maxNum = Integer.parseInt(nOptions.get("num"));
				if (maxNum > length) {
					maxNum = length;
				}
			} else {
				maxNum = length;
			}

			for (int i = 0; i < maxNum; i++) {
				Object aObj = list.get(i);
				ObjectInfo element = new ObjectInfo();
				element.setObjectId(ServiceRegistry.getService(
						ObjectHolderService.class).toId(aObj));
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
			classNames[i] = ClassHelper.makeClassName(classes[i]);
		}
		return classNames;
	}

	@Override
	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("ls [-f] [-m] [-c] [-a] [-d] [-n <num>] [<classname>]");

	}

}
