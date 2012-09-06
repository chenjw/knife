package com.chenjw.knife.agent.handler;

import org.springframework.context.ApplicationContext;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.NativeHelper;
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
		ClassLoaderHelper.view();

		do5(args, dispatcher);
		Agent.info("do finished!");
	}

	private void do1(Args args, CommandDispatcher dispatcher) {
		init();
		ContextManager.getInstance().put(Constants.THIS,
				getBean("applyService"));
		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		dispatcher.dispatch(new Command("invoke", "-t apply({\"id\":1})"));
		// dispatcher.dispatch(new Command("trace", "-f com.chenjw.* apply"));
	}

	private void do5(Args args, CommandDispatcher dispatcher) {

		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		// dispatcher.dispatch(new Command("invoke", "-t apply({\"id\":1})"));
		dispatcher.dispatch(new Command("find", "ApplyServiceImpl"));
		dispatcher.dispatch(new Command("cd", "0"));
		dispatcher.dispatch(new Command("trace", "-t apply"));
	}

	private void do4(Args args, CommandDispatcher dispatcher) {
		init();
		ContextManager.getInstance().put(Constants.THIS,
				getBean("applyService"));
		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		// dispatcher.dispatch(new Command("invoke", "-t apply({\"id\":1})"));
		dispatcher.dispatch(new Command("trace", "-t apply"));
	}

	private void do2(Args args, CommandDispatcher dispatcher) {
		init();
		ContextManager.getInstance().put(Constants.THIS,
				getBean("pageCommonDataFeeder"));
		dispatcher.dispatch(new Command("trace", "getData"));
	}

	private void do3(Args args, CommandDispatcher dispatcher) {
		for (Class<?> clazz : Agent.getAllLoadedClasses()) {
			if ("com.alibaba.china.credit.profile.dataFeeder.PageCommonDataFeeder"
					.equals(clazz.getName())) {
				ClassLoaderHelper.resetClassLoader(clazz);
				try {
					Class<?> cc = clazz
							.getClassLoader()
							.loadClass(
									"com.alibaba.china.credit.profile.param.DetailParametersVO");
					System.out.println("cc" + cc.getClassLoader());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(clazz.getClassLoader());
			}
		}

		dispatcher.dispatch(new Command("trace", "getData"));
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("do");
		argDef.setDesc("do some test.");
	}
}
