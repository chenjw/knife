package com.chenjw.knife.agent.handler;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.ArgDef;
import com.chenjw.knife.agent.handler.arg.Args;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

public class ViewCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher)
			throws IOException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		HotSpotDiagnosticMXBean rmxb = ManagementFactory
				.newPlatformMXBeanProxy(server,
						"com.sun.management:type=HotSpotDiagnostic",
						HotSpotDiagnosticMXBean.class);

		for (VMOption entry : rmxb.getDiagnosticOptions()) {
			Agent.println(entry.getName());
		}

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("view");
		argDef.setDesc("not support yet.");
	}

}
