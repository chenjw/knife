package com.chenjw.knife.agent.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.manager.ObjectRecordManager;

/**
 * 用于过滤掉代理类的调用信息输出
 * @author chenjw
 *
 */
public class ProxyMethodFilter implements Filter {

	
	private Pattern[] patterns;

	public ProxyMethodFilter() {
		String[] patternStrs=new String[]{
				"org.springframework.aop.*",
				"net.sf.cglib.*",
				"java.lang.reflect.Method.invoke",
				"org.springframework.util.ReflectionUtils.*"
		};
		List<Pattern> patternList=new ArrayList<Pattern>();
		for(String patternStr:patternStrs){
			patternList.add(Pattern.compile(patternStr));
		}
		patterns=patternList.toArray(new Pattern[patternList.size()]);
	}

	/**
	 * 为true表示需要过滤，为false表示不需要过滤
	 * 
	 * @param className
	 * @param methodName
	 * @return
	 */
	private boolean isMatch(Object thisObject,String className, String methodName) {
		String cName;
		if (thisObject != null) {
			cName=thisObject.getClass().getName();
		} else {
			cName=className;
		}
		String name = cName + "." + methodName;
		for(Pattern pattern:patterns){
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void doFilter(Event event, FilterChain chain) throws Exception {
		if (event instanceof MethodStartEvent) {
			MethodStartEvent e = (MethodStartEvent) event;
			if (isMatch(e.getThisObject(),e.getClassName(), e.getMethodName())) {
				return;
			}
		} else if (event instanceof MethodReturnEndEvent) {
			MethodReturnEndEvent e = (MethodReturnEndEvent) event;
			if (isMatch(e.getThisObject(),e.getClassName(), e.getMethodName())) {
				return;
			}

		} else if (event instanceof MethodExceptionEndEvent) {
			MethodExceptionEndEvent e = (MethodExceptionEndEvent) event;
			if (isMatch(e.getThisObject(),e.getClassName(), e.getMethodName())) {
				return;
			}
		}
		chain.doFilter(event);
	}
}
