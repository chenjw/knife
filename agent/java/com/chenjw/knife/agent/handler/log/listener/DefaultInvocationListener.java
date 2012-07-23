package com.chenjw.knife.agent.handler.log.listener;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.handler.log.InvocationListener;
import com.chenjw.knife.agent.handler.log.InvokeDepth;
import com.chenjw.knife.agent.handler.log.InvokeRecord;
import com.chenjw.knife.agent.handler.log.MethodFilter;

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
			Agent.println(d(InvokeDepth.getDep()) + msg);
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
		StringBuffer msg = new StringBuffer("[returns] ");
		if (result == null) {
			msg.append("null");
		} else {
			msg.append(InvokeRecord.toId(result) + result.getClass().getName());
		}
		try {
			Agent.println(d(InvokeDepth.getDep()) + msg);
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
		String msg = null;
		// e.printStackTrace();
		msg = "[throws] " + InvokeRecord.toId(e) + e;
		try {
			Agent.println(d(InvokeDepth.getDep()) + msg);
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
