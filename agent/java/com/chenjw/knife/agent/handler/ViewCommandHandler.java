package com.chenjw.knife.agent.handler;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandHandler;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

public class ViewCommandHandler implements CommandHandler {

	@Override
	public void handle(String[] args) {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			HotSpotDiagnosticMXBean rmxb = ManagementFactory
					.newPlatformMXBeanProxy(server,
							"com.sun.management:type=HotSpotDiagnostic",
							HotSpotDiagnosticMXBean.class);

			for (VMOption entry : rmxb.getDiagnosticOptions()) {
				Agent.print(entry.getName());
			}

		} catch (Exception e) {
			Agent.print(e.getClass().getName() + ":" + e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "view";
	}

}
