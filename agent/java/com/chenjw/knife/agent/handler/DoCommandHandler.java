package com.chenjw.knife.agent.handler;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.arg.Args;
import com.chenjw.knife.agent.handler.constants.Constants;
import com.chenjw.knife.core.Command;

public class DoCommandHandler implements CommandHandler {
	private ApplicationContext[] contexts = null;

	private void init() {
		if (contexts == null) {
			contexts = NativeHelper
					.findInstancesByClass(ApplicationContext.class);
		}
	}

	private Object getBean(String name) {
		for (ApplicationContext context : contexts) {
			Object bean = context.getBean(name);
			if (bean != null) {
				return bean;
			}
		}
		return null;
	}

	public void handle(Args args, CommandDispatcher dispatcher) {
		try {
			init();
			Context.put(Constants.THIS, getBean("testService"));
			// FileUtils.writeByteArrayToFile(new File(
			// "/home/chenjw/test/$Proxy0.class"), NativeHelper
			// .getClassBytes(Class.forName("$Proxy0")));
			// -f com.chenjw.*
			dispatcher.dispatch(new Command("invoke",
					"-f com.chenjw.* apply({})"));
			System.out.println("do finished!");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getName() {
		return "do";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {
	}
}
