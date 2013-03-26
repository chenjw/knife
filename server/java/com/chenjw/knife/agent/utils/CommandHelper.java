package com.chenjw.knife.agent.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.utils.ClassHelper;
import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.StringHelper;

public class CommandHelper {
	/**
	 * 从上下文中查找目标对象或类
	 * 
	 * @param expression
	 * @return
	 */
	public static ClassOrObject findTarget(String expression) {
		ClassOrObject target = new ClassOrObject();
		if (StringHelper.isBlank(expression)) {
			target.setObj(ServiceRegistry.getService(ContextService.class).get(
					Constants.THIS));

		} else if (StringHelper.isNumeric(expression)) {
			target.setObj(ServiceRegistry.getService(ObjectHolderService.class)
					.get(Integer.parseInt(expression)));

		} else {
			target.setClazz(ClassHelper.findClass(expression));
		}
		return target;
	}
	
	/**
	 * 根据表达式从上下文中找到需要的对象
	 * 
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static MethodInfo findMethod(String expression) throws Exception {
		if(StringHelper.isBlank(expression)){
			return null;
		}
		// 是否針對目標對象
		boolean isUserThisObject=true;
		expression=expression.trim();
		MethodInfo methodInfo = new MethodInfo();
		Method method = null;
		if (StringHelper.isNumeric(expression)) {
			method = ((Method[]) ServiceRegistry.getService(
					ContextService.class).get(Constants.METHOD_LIST))[Integer
					.parseInt(expression)];

		} else {
			if (expression.indexOf(".") != -1) {
				String className = StringHelper.substringBeforeLast(expression, ".");
				expression = StringHelper.substringAfterLast(expression, ".");
				Class<?> clazz = ClassHelper.findClass(className);
				if (clazz == null) {
					Agent.sendResult(ResultHelper.newErrorResult("class "
							+ className + " not found!"));
					return null;
				}
				
				Method[] methods = ReflectHelper.getMethods(clazz);
				for (Method tm : methods) {
					if (StringHelper.equals(tm.getName(), expression)) {
						isUserThisObject=false;
						method = tm;
						break;
					}
				}
			} else {
				Object obj = ServiceRegistry.getService(ContextService.class)
						.get(Constants.THIS);
				if (obj == null) {
					Agent.sendResult(ResultHelper.newErrorResult("not found!"));
					return null;
				}
				Method[] methods = obj.getClass().getMethods();
				for (Method tm : methods) {
					if (StringHelper.equals(tm.getName(), expression)) {
						method = tm;
						break;
					}
				}
			}
		}
		if (method == null) {
			Agent.sendResult(ResultHelper.newErrorResult("cant find method!"));
			return null;
		}
		methodInfo.setMethod(method);
		if (Modifier.isStatic(method.getModifiers())) {
			methodInfo.setClazz(method.getDeclaringClass());
			methodInfo.setThisObject(null);
		} else if(isUserThisObject) {
			Object thisObject = ServiceRegistry
					.getService(ContextService.class).get(Constants.THIS);
			methodInfo.setThisObject(thisObject);
			methodInfo.setClazz(thisObject.getClass());
		}
		return methodInfo;
	}
	
	public static class MethodInfo {
		private Object thisObject;
		private Class<?> clazz;
		private Method method;

		public Object getThisObject() {
			return thisObject;
		}

		public void setThisObject(Object thisObject) {
			this.thisObject = thisObject;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

	}
	
	public static class ClassOrObject {
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
}
