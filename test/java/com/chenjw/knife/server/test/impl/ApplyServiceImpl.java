package com.chenjw.knife.server.test.impl;

import com.chenjw.knife.server.test.ApplyModel;
import com.chenjw.knife.server.test.ApplyService;
import com.chenjw.knife.server.test.CheckService;
import com.chenjw.knife.server.test.DataService;
import com.chenjw.knife.server.test.ResultModel;

public class ApplyServiceImpl implements ApplyService {
	private final static String testStaticFinalString = "testStaticFinalString";
	private static String testStaticString = "testStaticString";
	private final static int testStaticInt = 9;
	private CheckService checkService;
	private DataService dataService;

	private boolean testBoolean = false;
	private byte testByte = 1;
	private char testChar = '2';
	private short testShort = 3;
	private final int testInt = 4;
	private long testLong = 5;
	private float testFloat = 6;
	private double testDouble = 7;

	private String testStr = "8";

	private String testPrivate(String t) {
		return "hello " + t;
	}

	public static String hello(String[] names) {
		String str = "";
		for (String name : names) {
			str += "hello " + name + "\n";
		}
		return str;

	}

	public ResultModel apply(ApplyModel apply) {
		// System.out.println(testStaticFinalString);
		// System.out.println(testStaticString);
		// check
		try {
			checkService.check(apply);
		} catch (Exception e) {
			// e.printStackTrace();
			ResultModel r = new ResultModel();
			r.setSuccess(false);
			r.setMsg(e.getLocalizedMessage());
			return r;
		}
		ApplyModel dbApply = dataService.find(apply.getId());
		if (dbApply == null) {
			dataService.insert(apply);
			ResultModel r = new ResultModel();
			r.setSuccess(true);
			r.setMsg("insert success");
			return r;
		} else {
			dataService.update(apply);
			ResultModel r = new ResultModel();
			r.setSuccess(true);
			r.setMsg("update success");
			return r;
		}
	}

	public void setCheckService(CheckService checkService) {
		this.checkService = checkService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

}
