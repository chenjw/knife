package com.chenjw.knife.agent.args;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.utils.StringHelper;

public class ArgDef {
	private List<OptionDesc> optionDescs = new ArrayList<OptionDesc>();
	private List<OptionDesc> argDescs = new ArrayList<OptionDesc>();
	private String commandName;
	private String def = "";
	private String desc = "";

	public OptionDesc getByKey(String name) {
		for (OptionDesc o : optionDescs) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		for (OptionDesc o : argDescs) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		return null;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	private int findEndOfEncloseStr(String def, int start) {
		int d = 0;
		char c = def.charAt(start);
		int i = start;
		do {
			if (c == '[' || c == '<') {
				d++;
			} else if (c == ']' || c == '>') {
				d--;
			}
			i++;
			if (i == def.length()) {
				break;
			} else {
				c = def.charAt(i);
			}
		} while (d != 0);
		return i;
	}

	public void setDef(String def) {
		this.def = def;

		int start = 0;
		int end = 0;
		for (int i = 0; i < def.length();) {
			char c = def.charAt(i);
			if (c == ' ') {
				i++;
				continue;
			}
			start = i;
			end = findEndOfEncloseStr(def, start);

			String str = def.substring(start, end);
			OptionDesc o = parse(str);
			parseOption(o);
			if (o.getFullName().startsWith("-")) {
				optionDescs.add(o);
			} else {
				argDescs.add(o);
			}
			i = end;

		}

	}

	private void parseOption(OptionDesc o) {
		String[] strs = o.getFullName().split(" ");
		String name = strs[0];
		o.setName(name);
		if (strs.length > 1) {
			List<String> values = new ArrayList<String>();
			for (int i = 1; i < strs.length; i++) {
				values.add(getUncloseStr(strs[i]));
			}
			o.setValueDefs(values.toArray(new String[values.size()]));
		}
	}

	private String getUncloseStr(String str) {
		if (str.startsWith("[")) {
			str = StringHelper.substringBefore(
					StringHelper.substringAfter(str, "["), "]");
		}
		if (str.startsWith("<")) {
			str = StringHelper.substringBefore(
					StringHelper.substringAfter(str, "<"), ">");
		}
		return str;
	}

	private OptionDesc parse(String str) {
		boolean isOptional = false;
		if (str.startsWith("[")) {
			isOptional = true;
		}
		str = getUncloseStr(str);
		OptionDesc option = new OptionDesc();
		option.setFullName(str);
		option.setOptional(isOptional);
		return option;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void addOptionDesc(String name, String desc) {
		if (name.startsWith("-")) {
			for (OptionDesc o : optionDescs) {
				if (name.equals(o.getName())) {
					o.setDesc(desc);
				}
			}
		} else {
			for (OptionDesc o : argDescs) {
				if (name.equals(o.getName())) {
					o.setDesc(desc);
				}
			}
		}
	}

	public List<OptionDesc> getOptionDescs() {
		return optionDescs;
	}

	public List<OptionDesc> getArgDescs() {
		return argDescs;
	}

	public void print() {

	}

	public String getDef() {
		return def;
	}

	public String getDesc() {
		return desc;
	}

}
