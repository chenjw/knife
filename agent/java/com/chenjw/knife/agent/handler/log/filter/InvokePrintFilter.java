package com.chenjw.knife.agent.handler.log.filter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.handler.log.Filter;
import com.chenjw.knife.agent.handler.log.FilterChain;
import com.chenjw.knife.agent.handler.log.InvokeDepth;
import com.chenjw.knife.agent.handler.log.InvokeRecord;
import com.chenjw.knife.agent.handler.log.Profiler;
import com.chenjw.knife.agent.handler.log.event.Event;
import com.chenjw.knife.agent.handler.log.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.handler.log.event.MethodStartEvent;
import com.chenjw.knife.utils.TimingHelper;

public class InvokePrintFilter implements Filter {

	private String d(int dep) {
		String s = "";
		for (int i = 0; i < dep; i++) {
			s += "--";
		}
		return s;
	}

	protected void onStart(MethodStartEvent event) {
		String className = event.getClassName();
		String methodName = event.getMethodName();
		Object thisObject = event.getThisObject();
		Object[] arguments = event.getArguments();
		StringBuffer msg = new StringBuffer("[invoke] ");
		String cn = null;
		if (thisObject != null) {
			cn = InvokeRecord.toId(thisObject)
					+ thisObject.getClass().getName();
		} else {
			cn = className;
		}
		msg.append(cn + "." + methodName);
		msg.append("(");
		boolean isFirst = true;
		for (Object arg : arguments) {
			if (isFirst) {
				isFirst = false;
			} else {
				msg.append(",");
			}
			if (arg == null) {
				msg.append("null");
			} else {
				msg.append(InvokeRecord.toId(arg) + arg.getClass().getName());
			}
		}
		msg.append(")");
		try {
			int dep = InvokeDepth.getDep();
			Agent.println(d(dep) + msg);
			TimingHelper.start(String.valueOf(dep));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	protected void onReturnEnd(MethodReturnEndEvent event) {
		Object result = event.getResult();
		int dep = InvokeDepth.getDep();
		StringBuffer msg = new StringBuffer("[return] ");
		if (result == null) {
			msg.append("null");
		} else if (result == Profiler.VOID) {
			msg.append("void");
		} else {
			msg.append(InvokeRecord.toId(result) + result.getClass().getName());
		}
		msg.append(" [" + TimingHelper.getMillisInterval(String.valueOf(dep))
				+ " ms]");

		Agent.println(d(dep) + msg);
	}

	protected void onExceptionEnd(MethodExceptionEndEvent event) {
		Throwable e = event.getE();
		int dep = InvokeDepth.getDep();
		StringBuffer msg = new StringBuffer("[throw] ");
		// e.printStackTrace();
		msg.append(InvokeRecord.toId(e));
		msg.append(e);
		msg.append(" [" + TimingHelper.getMillisInterval(String.valueOf(dep))
				+ " ms]");
		Agent.println(d(dep) + msg);
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
