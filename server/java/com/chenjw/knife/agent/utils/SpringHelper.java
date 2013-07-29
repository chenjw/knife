package com.chenjw.knife.agent.utils;

import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.invoke.InvokeResult;
import com.chenjw.knife.utils.invoke.MethodInvokeException;

public class SpringHelper {
	public static final String CLASS_FILE_SYSTEM_XML_APPLICATION_CONTEXT = "org.springframework.web.context.support.XmlWebApplicationContext";
	public static final String CLASS_APPLICATION_CONTEXT = "org.springframework.context.ApplicationContext";

	private static Class<?> getApplicationContextClass() {
		try {
			return Class.forName(CLASS_APPLICATION_CONTEXT);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Object getBeanById(String id) {
		Class<?> applicationContextClass = getApplicationContextClass();
		if (applicationContextClass == null) {
			return null;
		}
		Object[] objs = NativeHelper
				.findInstancesByClass(applicationContextClass);
		for (Object obj : objs) {
			InvokeResult r = null;
			try {
				r = ReflectHelper.invokeMethod(obj, "getBean",
						new Object[] { String.class }, new Object[] { id },
						null);
				if (r.isSuccess() && r.getResult() != null) {
					return r.getResult();
				}
			} catch (MethodInvokeException e) {

			}
		}
		return null;
	}
}
