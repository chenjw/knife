package com.chenjw.knife.agent.handler;

import org.springframework.context.ApplicationContext;

import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.server.test.TestService;
import com.chenjw.knife.server.test.impl.TestServiceImpl;

public class DoCommandHandler implements CommandHandler {
	private TestService p = new TestServiceImpl();
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

	public void handle(String[] args) {
		try {
			init();
			TestService t = (TestService) this.getBean("testService");
			t.apply(null);
			System.out.println(123);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getName() {
		return "do";
	}
}
