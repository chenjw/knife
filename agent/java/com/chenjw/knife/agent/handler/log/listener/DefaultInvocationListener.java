package com.chenjw.knife.agent.handler.log.listener;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.handler.log.InvocationListener;
import com.chenjw.knife.agent.handler.log.InvokeDepth;
import com.chenjw.knife.agent.handler.log.InvokeRecord;
import com.chenjw.knife.agent.handler.log.MethodFilter;
import com.chenjw.knife.utils.TimingHelper;

public class DefaultInvocationListener implements InvocationListener {
	private MethodFilter methodFilter;

	private boolean isLog(String className, String methodName) {
		if (methodFilter == null) {
			return true;
		} else {
			if (methodFilter.doFilter(className, methodName)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private String d(int dep) {
		String s = "";
		for (int i = 0; i < dep; i++) {
			s += "--";
		}
		return s;
	}

	@Override
	public void onStart(Object thisObject, String className, String methodName,
			Object[] arguments) {
		if (!isLog(className, methodName)) {
			return;
		}
		InvokeDepth.enter(thisObject);
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

	@Override
	public void onReturnEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Object result) {
		if (!isLog(className, methodName)) {
			return;
		}
		int dep = InvokeDepth.getDep();
		StringBuffer msg = new StringBuffer("[returns] ");
		if (result == null) {
			msg.append("null");
		} else {
			msg.append(InvokeRecord.toId(result) + result.getClass().getName());
		}
		msg.append(" [" + TimingHelper.getMillisInterval(String.valueOf(dep))
				+ " ms]");
		try {
			Agent.println(d(dep) + msg);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			InvokeDepth.leave(thisObject);
		}

	}

	@Override
	public void onExceptionEnd(Object thisObject, String className,
			String methodName, Object[] arguments, Throwable e) {
		if (!isLog(className, methodName)) {
			return;
		}
		int dep = InvokeDepth.getDep();
		StringBuffer msg = new StringBuffer("[throws] ");
		// e.printStackTrace();
		msg.append(InvokeRecord.toId(e));
		msg.append(e);
		msg.append(" [" + TimingHelper.getMillisInterval(String.valueOf(dep))
				+ " ms]");
		try {
			Agent.println(d(dep) + msg);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			InvokeDepth.leave(thisObject);
		}
	}

	public void setMethodFilter(MethodFilter methodFilter) {
		this.methodFilter = methodFilter;
	}

}
