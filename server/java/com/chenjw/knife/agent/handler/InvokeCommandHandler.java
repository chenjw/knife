package com.chenjw.knife.agent.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.bytecode.javassist.Helper;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.filter.Depth0Filter;
import com.chenjw.knife.agent.filter.DepthFilter;
import com.chenjw.knife.agent.filter.ExceptionFilter;
import com.chenjw.knife.agent.filter.Filter;
import com.chenjw.knife.agent.filter.FilterInvocationListener;
import com.chenjw.knife.agent.filter.FixThreadFilter;
import com.chenjw.knife.agent.filter.InstrumentClassLoaderFilter;
import com.chenjw.knife.agent.filter.InstrumentFilter;
import com.chenjw.knife.agent.filter.InvokeFinishFilter;
import com.chenjw.knife.agent.filter.InvokePrintFilter;
import com.chenjw.knife.agent.filter.PatternMethodFilter;
import com.chenjw.knife.agent.filter.ProxyMethodFilter;
import com.chenjw.knife.agent.filter.StopObjectsFilter;
import com.chenjw.knife.agent.filter.SystemOperationFilter;
import com.chenjw.knife.agent.filter.TimingFilter;
import com.chenjw.knife.agent.filter.TimingStopFilter;
import com.chenjw.knife.agent.service.ByteCodeService;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ParseHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.SpringHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.StringHelper;
import com.chenjw.knife.utils.invoke.InvokeResult;
import com.chenjw.knife.utils.invoke.MethodInvokeException;

public class InvokeCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {
		boolean isTrace = args.option("-t") != null;
		configStrategy(args);
		invokeMethod(isTrace, args.arg("invoke-expression"));
	}

	private void configStrategy(Args args) throws Exception {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new FixThreadFilter(Thread.currentThread()));
		filters.add(new ExceptionFilter());
		filters.add(new TimingStopFilter());

		Map<String, String> sbOptions = args.option("-sb");
		if (sbOptions != null) {
			// clear instrument
			ServiceRegistry.getService(ByteCodeService.class).clear();
			filters.add(new StopObjectsFilter(getSpringBeansByIds(sbOptions
					.get("stop-bean-ids"))));
		}
		Map<String, String> tOptions = args.option("-t");
		if (tOptions != null) {
			filters.add(new InstrumentClassLoaderFilter());
			filters.add(new InstrumentFilter());
		}
		Map<String, String> fOptions = args.option("-f");
		if (fOptions != null) {
			filters.add(new PatternMethodFilter(fOptions
					.get("filter-expression")));
		}
		filters.add(new ProxyMethodFilter());
		filters.add(new DepthFilter());
		if (tOptions == null) {
			filters.add(new Depth0Filter());
		}
		filters.add(new TimingFilter());
		filters.add(new InvokeFinishFilter());
		filters.add(new InvokePrintFilter());
		Profiler.listener = new FilterInvocationListener(filters);
	}

	private Object[] getSpringBeansByIds(String ids) {
		List<Object> objs = new ArrayList<Object>();
		for (String id : ids.split(",")) {
			Object obj = SpringHelper.getBeanById(id);
			if (obj != null) {
				objs.add(obj);
			}
		}
		return objs.toArray(new Object[objs.size()]);
	}

	private void invokeMethod(boolean isTrace, String methodSig)
			throws Exception {

		String argStr = methodSig;
		String m = StringHelper.substringBefore(argStr, "(");
		m = m.trim();
		Method method = null;
		if (StringHelper.isNumeric(m)) {
			method = ((Method[]) ServiceRegistry.getService(
					ContextService.class).get(Constants.METHOD_LIST))[Integer
					.parseInt(m)];
		} else {
			if (m.indexOf(".") != -1) {
				String className = StringHelper.substringBeforeLast(m, ".");
				m = StringHelper.substringAfterLast(m, ".");
				Class<?> clazz = NativeHelper.findLoadedClass(className);
				if (clazz == null) {
					clazz = Helper.findClass(className);
				}
				if (clazz == null) {
					Agent.sendResult(ResultHelper.newErrorResult("class "
							+ className + " not found!"));
					return;
				}
				Method[] methods = ReflectHelper.getMethods(clazz);
				for (Method tm : methods) {
					if (tm.getName().equals(m)) {
						if (Modifier.isStatic(tm.getModifiers())) {
							method = tm;
							break;
						}
					}
				}

			} else {
				Object obj = ServiceRegistry.getService(ContextService.class)
						.get(Constants.THIS);
				if (obj == null) {
					Agent.sendResult(ResultHelper.newErrorResult("not found!"));
					return;
				}
				Method[] methods = ReflectHelper.getMethods(obj.getClass());
				for (Method tm : methods) {
					if (tm.getName().equals(m)) {
						method = tm;
						break;
					}
				}
			}
		}
		if (method == null) {
			Agent.sendResult(ResultHelper.newErrorResult("method not found!"));
			return;
		}
		Object[] mArgs = ParseHelper.parseMethodArgs(
				StringHelper.substringBeforeLast(
						StringHelper.substringAfter(argStr, "("), ")"),
				method.getParameterTypes());

		if (Modifier.isStatic(method.getModifiers())) {
			invoke(isTrace, method, null, mArgs);
		} else {
			invoke(isTrace,
					method,
					ServiceRegistry.getService(ContextService.class).get(
							Constants.THIS), mArgs);
		}
	}

	private void invoke(boolean isTrace, Method method, Object thisObject,
			Object[] args) throws Exception {
		// 重置classloader
		ClassLoaderHelper.resetClassLoader(method.getDeclaringClass());
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		Class<?> clazz = null;
		if (isStatic) {
			thisObject = null;
			clazz = method.getDeclaringClass();
		} else {
			clazz = thisObject.getClass();
		}
		try {
			Profiler.start(thisObject, clazz.getName(), method.getName(), args,
					null, -1);
			if (isTrace) {
				Profiler.profile(thisObject, method);
			}
			InvokeResult r = null;
			if (isStatic) {
				r = ReflectHelper.invokeStaticMethod(method, args);
			} else {
				r = ReflectHelper.invokeMethod(thisObject, method, args);
			}

			if (r.isSuccess()) {
				Profiler.returnEnd(thisObject, clazz.getName(),
						method.getName(), args, r.getResult());
			} else {
				Profiler.exceptionEnd(thisObject, clazz.getName(),
						method.getName(), args, r.getE());
			}
		} catch (MethodInvokeException e) {
			Profiler.exceptionEnd(thisObject, clazz.getName(),
					method.getName(), args, e);
			throw e;
		}
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("invoke [-f <filter-expression>] [-t] [-sb <stop-bean-ids>] <invoke-expression>");

	}

}
