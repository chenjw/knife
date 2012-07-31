package com.chenjw.knife.agent.handler;

import org.springframework.context.ApplicationContext;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.arg.ArgDef;
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
		init();
		Context.put(Constants.THIS, getBean("applyService"));
		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		// dispatcher.dispatch(new Command("invoke", "apply({\"id\":1})"));
		dispatcher.dispatch(new Command("trace", "-f com.chenjw.* apply"));
		Agent.println("do finished!");
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("do");
		argDef.setDesc("do some test.");
	}
}
