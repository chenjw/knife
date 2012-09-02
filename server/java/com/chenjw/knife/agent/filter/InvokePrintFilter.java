package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.manager.InvokeDepthManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.manager.TimingManager;
import com.chenjw.knife.agent.utils.ToStringHelper;

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
			cn = ObjectRecordManager.getInstance().toId(thisObject)
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
				msg.append(ObjectRecordManager.getInstance().toId(arg)
						+ ToStringHelper.toString(arg));
			}
		}
		msg.append(")");
		int lineNum = event.getLineNum();
		if (lineNum == -1) {
			msg.append(" <unknow>");
		} else {
			String baseClassName = event.getFileName();
			msg.append(" <" + baseClassName + ":");
			msg.append(event.getLineNum());
			msg.append(">");
		}

		try {
			int dep = InvokeDepthManager.getInstance().getDep();
			Agent.info(d(dep) + msg);
			// TimingManager.getInstance().start(String.valueOf(dep));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	protected void onReturnEnd(MethodReturnEndEvent event) {
		Object result = event.getResult();
		int dep = InvokeDepthManager.getInstance().getDep();
		StringBuffer msg = new StringBuffer("[return] ");
		if (result == null) {
			msg.append("null");
		} else if (result == Profiler.VOID) {
			msg.append("void");
		} else {
			msg.append(ObjectRecordManager.getInstance().toId(result)
					+ ToStringHelper.toString(result));
		}
		msg.append(" ["
				+ TimingManager.getInstance().getMillisInterval(
						String.valueOf(dep)) + " ms]");

		Agent.info(d(dep) + msg);
	}

	protected void onExceptionEnd(MethodExceptionEndEvent event) {
		Throwable e = event.getE();
		int dep = InvokeDepthManager.getInstance().getDep();
		StringBuffer msg = new StringBuffer("[throw] ");
		// e.printStackTrace();
		msg.append(ObjectRecordManager.getInstance().toId(e));
		msg.append(e);
		msg.append(" ["
				+ TimingManager.getInstance().getMillisInterval(
						String.valueOf(dep)) + " ms]");
		Agent.info(d(dep) + msg);
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
