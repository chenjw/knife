package com.chenjw.knife.agent.handler.arg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class Args {
	private Map<String, String[]> argMap = new HashMap<String, String[]>();

	private String[] args;

	public Args(String argStr, Map<String, Integer> argDecls) {
		if (argStr == null) {
			return;
		}
		String[] args = argStr.split(" ");
		int i = 0;
		List<String> argList = new ArrayList<String>();
		while (i < args.length) {
			String k = args[i];
			Integer num = argDecls.get(k);
			if (num == null) {
				argList.add(k);
				i++;
			} else {
				String[] aa = new String[num];
				for (int j = 0; j < num; j++) {
					aa[j] = args[i + j + 1];
				}
				argMap.put(k, aa);
				i += num + 1;
			}
		}
		this.args = argList.toArray(new String[argList.size()]);
	}

	public String[] arg(String key) {
		return argMap.get(key);
	}

	public String getArgStr() {
		return StringUtils.join(args, " ");
	}

	public String arg(int index) {
		if (args == null) {
			return null;
		}
		if (index >= args.length) {
			return null;
		}
		return args[index];
	}
}
