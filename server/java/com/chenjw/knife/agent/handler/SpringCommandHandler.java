package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.SpringHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.invoke.InvokeResult;
import com.chenjw.knife.utils.invoke.MethodInvokeException;

public class SpringCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		String parentId = args.arg("parent-id");
		String filePath = args.arg("file-path");
		Object context = createApplicationContext(Integer.parseInt(parentId),
				"file:" + filePath);
		if (context == null) {
			Agent.sendResult(ResultHelper
					.newErrorResult("spring context create fail!"));
		} else {
			Agent.sendResult(ResultHelper.newResult("spring context created "
					+ ServiceRegistry.getService(ObjectHolderService.class)
							.toId(context)));
		}
	}

	private Object createApplicationContext(int parentId, String filePath) {

		// XmlWebApplicationContext context = new XmlWebApplicationContext();
		// context.setConfigLocation("classpath:com/chenjw/knife/server/context.xml");
		// // context.setParent(parent)
		// context.refresh();
		Object parent = ServiceRegistry.getService(ObjectHolderService.class)
				.get(parentId);
		if (parent == null) {
			return null;
		}
		InvokeResult r = null;
		try {
			r = ReflectHelper.invokeConstructor(
					SpringHelper.CLASS_FILE_SYSTEM_XML_APPLICATION_CONTEXT,
					new Object[0], new Object[0], parent.getClass()
							.getClassLoader());
			if (!r.isSuccess()) {
				return null;
			}
			Object newContext = r.getResult();
			InvokeResult t = ReflectHelper.invokeMethod(newContext,
					"setConfigLocation", new Object[] { String.class },
					new Object[] { filePath }, parent.getClass()
							.getClassLoader());
			if (!t.isSuccess()) {
				r.setE(t.getE());
			}
			t = ReflectHelper
					.invokeMethod(
							newContext,
							"setParent",
							new Object[] { SpringHelper.CLASS_APPLICATION_CONTEXT },
							new Object[] { parent }, parent.getClass()
									.getClassLoader());
			if (!t.isSuccess()) {
				r.setE(t.getE());
			}
			t = ReflectHelper.invokeMethod(newContext, "refresh",
					new Object[0], new Object[0], parent.getClass()
							.getClassLoader());
			if (!t.isSuccess()) {
				r.setE(t.getE());
			}
			r.setResult(newContext);
		} catch (MethodInvokeException e) {
			e.printStackTrace();
			return null;
		}
		if (r.isSuccess()) {
			return r.getResult();
		} else {
			r.getE().printStackTrace();
			return null;
		}
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("spring <parent-id> <file-path>");

	}
}
