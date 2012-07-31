package com.chenjw.knife.agent.handler.arg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class Args {
	private Map<String, Map<String, String>> optionMap = new HashMap<String, Map<String, String>>();

	private Map<String, String> argMap = new HashMap<String, String>();

	public Args() {

	}

	public void parse(String argStr, ArgDef argDef) throws Exception {
		if (argStr == null) {
			return;
		}
		String tmp = argStr;
		// read options
		int i = 0;
		for (i = 0; i < tmp.length();) {
			char k = tmp.charAt(i);
			if (k == ' ') {
				i++;
				continue;
			}
			if (k == '-') {
				int start = i;
				int end = StringUtils.indexOf(tmp, ' ', start);
				if (end == -1) {
					end = tmp.length();
				}
				String key = tmp.substring(i, end);
				OptionDesc optionDesc = argDef.getByKey(key);
				if (optionDesc == null) {
					throw new Exception("option <" + key + "> not found!");
				}
				Map<String, String> values = new HashMap<String, String>();
				String[] valueDefs = optionDesc.getValueDefs();
				if (valueDefs != null) {
					for (String valueDef : valueDefs) {
						start = end + 1;
						end = StringUtils.indexOf(tmp, ' ', start);
						String value = tmp.substring(start, end);
						values.put(valueDef, value);
					}
				}
				i = end;
				optionMap.put(key, values);
				continue;
			} else {
				break;
			}
		}
		Map<String, String> argMap = new HashMap<String, String>();
		// read args
		int j = 0;
		int start = i;
		int end = 0;
		for (OptionDesc argDesc : argDef.getArgDescs()) {

			if (j == argDef.getArgDescs().size() - 1) {
				end = tmp.length();
			} else {
				end = StringUtils.indexOf(tmp, ' ', start);
			}
			String value = tmp.substring(start, end);
			if (StringUtils.isBlank(value)) {
				value = null;
			}
			argMap.put(argDesc.getName(), value);
			start = end + 1;
			j++;
		}

		this.argMap = argMap;
	}

	// private Integer getNum(ArgDef argDef, String k) {
	// OptionDesc desc = argDef.getByName(k);
	// if (desc == null) {
	// return null;
	// }
	// String[] value = argDef.getByName(k).getValue();
	// if (value == null) {
	// return null;
	// }
	// return value.length;
	// }

	public Map<String, String> option(String optionName) {
		return optionMap.get(optionName);
	}

	public String arg(String argName) {
		return argMap.get(argName);
	}
}
