package com.chenjw.knife.server;

import org.springframework.web.context.support.XmlWebApplicationContext;

import com.chenjw.knife.server.test.ApplyModel;
import com.chenjw.knife.server.test.ApplyService;

public class Main1 {
	public static int i = 0;
	public static final String PID_ID = "test_main";

	private Main1(int i) {
		Main1.i = i;
	}

	public Main1(String str) {
		System.out.println(str);
	}

	public static void main(String[] args) {

		// JvmUtils.writePid(PID_ID);
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocation("classpath:com/chenjw/knife/server/context.xml");
		context.refresh();
		while (true) {
			// System.out.println(i++);
			try {

				Thread.sleep(10);
				try {
					ApplyService testService = (ApplyService) context
							.getBean("applyService");
					ApplyModel apply = new ApplyModel();
					apply.setId(1);
					testService.apply(apply);
				} catch (Throwable e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
			}
		}
	}
}