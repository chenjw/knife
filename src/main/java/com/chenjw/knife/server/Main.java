package com.chenjw.knife.server;

import org.springframework.web.context.support.XmlWebApplicationContext;

import com.chenjw.knife.utils.JvmUtils;

public class Main {
	public static int i = 0;
	public static final String PID_ID = "test_main";

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
					// TestPojo t = new TestPojo();
					// t.test2("bb");
					// t.test1("aa", "b");

				} catch (Throwable e) {

				}

			} catch (InterruptedException e) {
			}
		}
	}
}
