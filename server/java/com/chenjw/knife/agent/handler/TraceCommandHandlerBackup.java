package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.filter.CurrentContextClassLoaderFilter;
import com.chenjw.knife.agent.filter.Depth0Filter;
import com.chenjw.knife.agent.filter.DepthFilter;
import com.chenjw.knife.agent.filter.EnterLeavePrintFilter;
import com.chenjw.knife.agent.filter.ExceptionFilter;
import com.chenjw.knife.agent.filter.Filter;
import com.chenjw.knife.agent.filter.FilterInvocationListener;
import com.chenjw.knife.agent.filter.InstrumentClassLoaderFilter;
import com.chenjw.knife.agent.filter.InstrumentEnterLeaveFilter;
import com.chenjw.knife.agent.filter.InstrumentFilter;
import com.chenjw.knife.agent.filter.InvokePrintFilter;
import com.chenjw.knife.agent.filter.PatternMethodFilter;
import com.chenjw.knife.agent.filter.ProxyMethodFilter;
import com.chenjw.knife.agent.filter.StatPrintFilter;
import com.chenjw.knife.agent.filter.SystemOperationFilter;
import com.chenjw.knife.agent.filter.TimingFilter;
import com.chenjw.knife.agent.filter.TimingStopFilter;
import com.chenjw.knife.agent.filter.TraceMethodFilter;
import com.chenjw.knife.agent.filter.TraceTimesCountFilter;
import com.chenjw.knife.agent.service.CommandStatusService;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.CommandHelper;
import com.chenjw.knife.agent.utils.CommandHelper.MethodInfo;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.utils.ClassHelper;
import com.chenjw.knife.utils.StringHelper;

public class TraceCommandHandlerBackup implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher)
			throws Exception {

		MethodInfo methodInfo = CommandHelper.findMethod(args
				.arg("trace-expression"));
		if (methodInfo != null) {
			configStrategy(args, methodInfo);
			trace(methodInfo);
			// 不结束，等待消息
			ServiceRegistry.getService(CommandStatusService.class).waitResult();
		} else {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
		}

	}

	private void trace(MethodInfo methodInfo) {
		ClassLoaderHelper.resetClassLoader(methodInfo.getMethod()
				.getDeclaringClass());
		Profiler.profileEnterLeave(methodInfo.getMethod());
	}

	private void configStrategy(Args args, MethodInfo methodInfo)
			throws Exception {
		int traceNum = 1;
		Map<String, String> nOptions = args.option("-n");
		Map<String, String> cOptions = args.option("-c");
		Map<String, String> tOptions = args.option("-t");
		Map<String, String> fOptions = args.option("-f");
		if (nOptions != null) {
			traceNum = Integer.parseInt(nOptions.get("trace-num"));
		}
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new SystemOperationFilter());
		filters.add(new ExceptionFilter());
		filters.add(new TimingStopFilter());
		filters.add(new CurrentContextClassLoaderFilter());
		filters.add(new InstrumentClassLoaderFilter());
		if (tOptions != null || cOptions!=null) {
			filters.add(new InstrumentFilter());
		}
		filters.add(new InstrumentEnterLeaveFilter());
		if (fOptions != null) {
			filters.add(new PatternMethodFilter(fOptions
					.get("filter-expression")));
		}
		filters.add(new ProxyMethodFilter());
		filters.add(new TraceMethodFilter(methodInfo.getThisObject(),
				methodInfo.getClazz(), methodInfo.getMethod()));
		filters.add(new TraceTimesCountFilter(traceNum));
		filters.add(new EnterLeavePrintFilter());
		filters.add(new DepthFilter());
		if (tOptions == null && cOptions==null) {
			filters.add(new Depth0Filter());
		}
		filters.add(new TimingFilter());
        if(cOptions!=null){
            filters.add(new StatPrintFilter());
        }
        else{
            filters.add(new InvokePrintFilter());
        }
		Profiler.listener = new FilterInvocationListener(filters);
	}

	public static String getParamClassNames(Class<?>[] classes) {
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = ClassHelper.makeClassName(classes[i]);
		}
		return StringHelper.join(classNames, ",");
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("trace [-f <filter-expression>] [-n <trace-num>]  [-c] [-t] <trace-expression>");

	}
}
