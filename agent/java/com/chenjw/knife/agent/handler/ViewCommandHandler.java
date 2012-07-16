package com.chenjw.knife.agent.handler;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.management.MBeanServer;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandDispatcher;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.handler.arg.Args;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

public class ViewCommandHandler implements CommandHandler {

	@Override
	public void handle(Args args, CommandDispatcher dispatcher) {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			HotSpotDiagnosticMXBean rmxb = ManagementFactory
					.newPlatformMXBeanProxy(server,
							"com.sun.management:type=HotSpotDiagnostic",
							HotSpotDiagnosticMXBean.class);

			for (VMOption entry : rmxb.getDiagnosticOptions()) {
				Agent.println(entry.getName());
			}

		} catch (Exception e) {
			Agent.println(e.getClass().getName() + ":" + e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "view";
	}

	@Override
	public void declareArgs(Map<String, Integer> argDecls) {

	}

}
