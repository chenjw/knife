package com.chenjw.knife.agent.filter;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;
import com.chenjw.knife.agent.service.ObjectRecordService;
import com.chenjw.knife.agent.service.TimingService;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.model.MethodExceptionEndInfo;
import com.chenjw.knife.core.model.MethodReturnEndInfo;
import com.chenjw.knife.core.model.MethodStartInfo;
import com.chenjw.knife.core.model.ObjectInfo;
import com.chenjw.knife.core.result.Result;

public class InvokePrintFilter implements Filter {

	protected void onStart(MethodStartEvent event) {
		MethodStartInfo info = new MethodStartInfo();

		Object thisObject = event.getThisObject();

		if (thisObject != null) {
			info.setThisObjectId(ObjectRecordService.getInstance().toId(
					thisObject));
			info.setClassName(thisObject.getClass().getName());
		} else {
			info.setClassName(event.getClassName());
		}
		info.setMethodName(event.getMethodName());

		List<ObjectInfo> argInfos = new ArrayList<ObjectInfo>();
		for (Object arg : event.getArguments()) {
			if (arg == null) {
				argInfos.add(null);
			} else {
				ObjectInfo argInfo = new ObjectInfo();
				argInfo.setObjectId(ObjectRecordService.getInstance().toId(arg));
				argInfo.setValueString(ToStringHelper.toString(arg));
				argInfos.add(argInfo);
			}
		}
		info.setArguments(argInfos.toArray(new ObjectInfo[argInfos.size()]));
		info.setLineNum(event.getLineNum());
		info.setFileName(event.getFileName());
		info.setDepth(InvokeDepthService.getInstance().getDep());
		Result<MethodStartInfo> result = new Result<MethodStartInfo>();
		result.setContent(info);
		result.setSuccess(true);
		Agent.sendResult(result);

	}

	protected void onReturnEnd(MethodReturnEndEvent event) {
		MethodReturnEndInfo info = new MethodReturnEndInfo();
		Object r = event.getResult();
		if (r == Profiler.VOID) {
			info.setVoid(true);

		} else {
			info.setVoid(false);
			if (r != null) {
				ObjectInfo rInfo = new ObjectInfo();
				rInfo.setObjectId(ObjectRecordService.getInstance().toId(r));
				rInfo.setValueString(ToStringHelper.toString(r));
				info.setResult(rInfo);
			}
		}
		int dep = InvokeDepthService.getInstance().getDep();
		info.setDepth(dep);
		info.setTime(TimingService.getInstance().getMillisInterval(
				String.valueOf(dep)));
		Result<MethodReturnEndInfo> result = new Result<MethodReturnEndInfo>();
		result.setContent(info);
		result.setSuccess(true);
		Agent.sendResult(result);
	}

	protected void onExceptionEnd(MethodExceptionEndEvent event) {

		MethodExceptionEndInfo info = new MethodExceptionEndInfo();
		Throwable e = event.getE();
		ObjectInfo rInfo = new ObjectInfo();
		rInfo.setObjectId(ObjectRecordService.getInstance().toId(e));
		rInfo.setValueString(ToStringHelper.toString(e));
		info.setE(rInfo);

		int dep = InvokeDepthService.getInstance().getDep();
		info.setDepth(dep);
		info.setTime(TimingService.getInstance().getMillisInterval(
				String.valueOf(dep)));
		Result<MethodExceptionEndInfo> result = new Result<MethodExceptionEndInfo>();
		result.setContent(info);
		result.setSuccess(true);
		Agent.sendResult(result);

	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			onStart((MethodStartEvent) event);
		} else if (event instanceof MethodReturnEndEvent) {
			onReturnEnd((MethodReturnEndEvent) event);
		} else if (event instanceof MethodExceptionEndEvent) {
			onExceptionEnd((MethodExceptionEndEvent) event);
		}
		chain.doFilter(event);
	}

}
