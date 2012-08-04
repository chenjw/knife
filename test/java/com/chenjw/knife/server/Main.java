package com.chenjw.knife.server;

import org.springframework.web.context.support.XmlWebApplicationContext;

import com.chenjw.knife.server.test.ApplyModel;
import com.chenjw.knife.server.test.ApplyService;
import com.chenjw.knife.server.test.utils.JvmUtils;

public class Main {
	public static int i = 0;
	public static final String PID_ID = "test_main";

	private Main(int i) {
		Main.i = i;
	}

	public Main(String str) {
		System.out.println(str);
	}

	public static void main(String[] args) {
		JvmUtils.writePid(PID_ID);
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocation("classpath:com/chenjw/knife/server/context.xml");
		context.refresh();
		while (true) {
			// System.out.println(i++);
			try {

				Thread.sleep(3000);
				try {
					ApplyService testService = (ApplyService) context
							.getBean("applyService");
					ApplyModel apply = new ApplyModel();
					apply.setId(1);
					testService.apply(apply);
				} catch (Throwable e) {
					e.printStackTrace();
				}

			} catch (InterruptedException e) {
			}
		}
	}
}
