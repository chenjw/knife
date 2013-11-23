package com.chenjw.knife.agent.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
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
import com.chenjw.knife.agent.filter.StatPrintFilter;
import com.chenjw.knife.agent.filter.StopObjectsFilter;
import com.chenjw.knife.agent.filter.SystemOperationFilter;
import com.chenjw.knife.agent.filter.TimingFilter;
import com.chenjw.knife.agent.filter.TimingStopFilter;
import com.chenjw.knife.agent.service.ByteCodeService;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.CommandHelper;
import com.chenjw.knife.agent.utils.CommandHelper.MethodInfo;
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
	    Map<String, String> cOptions = args.option("-c");
	    Map<String, String> sbOptions = args.option("-sb");
	    Map<String, String> tOptions = args.option("-t");
	    Map<String, String> fOptions = args.option("-f");
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new FixThreadFilter(Thread.currentThread()));
		filters.add(new ExceptionFilter());
		filters.add(new TimingStopFilter());
		if (sbOptions != null) {
			// clear instrument
			ServiceRegistry.getService(ByteCodeService.class).clear();
			filters.add(new StopObjectsFilter(getSpringBeansByIds(sbOptions
					.get("stop-bean-ids"))));
		}
		if (tOptions != null || cOptions!=null) {
			filters.add(new InstrumentClassLoaderFilter());
			filters.add(new InstrumentFilter());
		}
		if (fOptions != null) {
			filters.add(new PatternMethodFilter(fOptions
					.get("filter-expression")));
		}
		filters.add(new ProxyMethodFilter());
		filters.add(new DepthFilter());
		if (tOptions == null && cOptions==null) {
			filters.add(new Depth0Filter());
		}
		filters.add(new TimingFilter());
		filters.add(new InvokeFinishFilter());
		if(cOptions!=null){
		    filters.add(new StatPrintFilter());
		}
		else{
		    filters.add(new InvokePrintFilter());
		}
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
		
		MethodInfo methodInfo=CommandHelper.findMethod(m);
		
		if (methodInfo == null) {
			Agent.sendResult(ResultHelper.newErrorResult("method not found!"));
			return;
		}
		Method method =methodInfo.getMethod();
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
					null, "-1");
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

		argDef.setDefinition("invoke [-f <filter-expression>] [-t] [-c] [-sb <stop-bean-ids>] <invoke-expression>");

	}

}
