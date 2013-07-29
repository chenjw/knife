package com.chenjw.knife.agent.constants;

/**
 * 一些常量
 * 
 * @author chenjw
 *
 */
public class Constants {
	// 当前对象在上下文中的key
	public static final String THIS = "this";
	// 当前class列表在上下文中的key（"find"执行后）
	public static final String CLASS_LIST = "class_list";
	// 当前方法列表在上下文中的key（"ls -m"执行后）
	public static final String METHOD_LIST = "method_list";
	// 当前构造列表在上下文中的key（"ls -c"执行后）
	public static final String CONSTRUCTOR_LIST = "constructor_list";
}
