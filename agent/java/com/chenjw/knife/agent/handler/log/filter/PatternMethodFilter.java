package com.chenjw.knife.agent.handler.log.filter;

import java.util.regex.Pattern;

import com.chenjw.knife.agent.handler.log.MethodFilter;

public class PatternMethodFilter implements MethodFilter {
	private Pattern pattern;

	public PatternMethodFilter(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	@Override
	public boolean doFilter(String className, String methodName) {
		String name = className + "." + methodName;
		if (pattern.matcher(name).matches()) {
			return true;
		}
		return false;
	}
}
