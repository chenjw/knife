package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.SpringHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.result.CommandInfo;
import com.chenjw.knife.core.model.result.CommandListInfo;

public class DoCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {
		ClassLoaderHelper.view();
		do6(args, dispatcher);
	}

	private void do1(Args args, CommandDispatcher dispatcher) {

		ServiceRegistry.getService(ContextService.class).put(Constants.THIS,
				SpringHelper.getBeanById("applyService"));
		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		send(new CommandInfo[] { new CommandInfo("invoke",
				"-t apply({\"id\":1})") });
		// dispatcher.dispatch(new Command("trace", "-f com.chenjw.* apply"));
	}

	private void do5(Args args, CommandDispatcher dispatcher) {

		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		// dispatcher.dispatch(new Command("invoke", "-t apply({\"id\":1})"));
		send(new CommandInfo[] {
				//
				new CommandInfo("find", "ApplyServiceImpl"),
				new CommandInfo("cd", "0"),
				new CommandInfo("trace", "-t apply") });
	}
	
	   private void do6(Args args, CommandDispatcher dispatcher) {

	        // dispatcher.dispatch(new Command("invoke",
	        // "-f com.chenjw.* apply({\"id\":1})"));
	        // dispatcher.dispatch(new Command("invoke", "-t apply({\"id\":1})"));
	        send(new CommandInfo[] {
	                //
	                new CommandInfo("find", "OfficialAccountManagerImpl"),
	                new CommandInfo("cd", "0"),
	                new CommandInfo("invoke", "-t queryUserFollowAccount(\"2088102011188231\",null,null,null,1,10)") });
	    }

	private void send(CommandInfo... infos) {
		CommandListInfo list = new CommandListInfo();
		list.setCommands(infos);
		Agent.sendResult(ResultHelper.newResult(list));
	}

	private void do4(Args args, CommandDispatcher dispatcher) {
		ServiceRegistry.getService(ContextService.class).put(Constants.THIS,
				SpringHelper.getBeanById("applyService"));
		// dispatcher.dispatch(new Command("invoke",
		// "-f com.chenjw.* apply({\"id\":1})"));
		// dispatcher.dispatch(new Command("invoke", "-t apply({\"id\":1})"));
		send(new CommandInfo[] { new CommandInfo("trace", "-t apply") });
	}

	private void do2(Args args, CommandDispatcher dispatcher) {
		ServiceRegistry.getService(ContextService.class).put(Constants.THIS,
				SpringHelper.getBeanById("pageCommonDataFeeder"));
		send(new CommandInfo[] { new CommandInfo("trace", "getData") });
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
		send(new CommandInfo[] { new CommandInfo("trace", "getData") });
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setDefinition("do");

	}
}
