package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.ReflectHelper;
import com.chenjw.knife.agent.utils.invoke.InvokeResult;
import com.chenjw.knife.agent.utils.invoke.MethodInvokeException;

public class SpringCommandHandler implements CommandHandler {
	private static final String CLASS_FILE_SYSTEM_XML_APPLICATION_CONTEXT = "org.springframework.web.context.support.XmlWebApplicationContext";
	private static final String CLASS_APPLICATION_CONTEXT = "org.springframework.context.ApplicationContext";

	public void handle(Args args, CommandDispatcher dispatcher) {
		String parentId = args.arg("parent-id");
		String filePath = args.arg("file-path");
		Object context = createApplicationContext(Integer.parseInt(parentId),
				"file:" + filePath);
		if (context == null) {
			Agent.info("spring context create fail!");
		} else {
			Agent.info("spring context created "
					+ ObjectRecordManager.getInstance().toId(context));
		}
	}

	private Object createApplicationContext(int parentId, String filePath) {

		// XmlWebApplicationContext context = new XmlWebApplicationContext();
		// context.setConfigLocation("classpath:com/chenjw/knife/server/context.xml");
		// // context.setParent(parent)
		// context.refresh();
		Object parent = ObjectRecordManager.getInstance().get(parentId);
		if (parent == null) {
			Agent.info("not found");
			return null;
		}
		InvokeResult r = null;
		try {
			r = ReflectHelper.invokeConstructor(
					CLASS_FILE_SYSTEM_XML_APPLICATION_CONTEXT, new Object[0],
					new Object[0], parent.getClass().getClassLoader());
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
					.invokeMethod(newContext, "setParent",
							new Object[] { CLASS_APPLICATION_CONTEXT },
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
			Agent.info(r.getE().getLocalizedMessage());
			return null;
		}
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("spring");
		argDef.setDef("<parent-id> <file-path>");
		argDef.setDesc("create spring context");
		argDef.addOptionDesc("parent-id", "parent spring context obj-id");
		argDef.addOptionDesc("file-path", "spring xml file path");
	}
}
