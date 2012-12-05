package com.chenjw.knife.client.core;


/**
 * 是否可设置自动完成值
 * 
 * @author chenjw
 *
 */
public interface Completable {

	/**
	 * 设置参数的自动完成
	 * 
	 * @param strs
	 */
	public void setArgCompletors(String[] strs);
	
	/**
	 * 设置命令的自动完成
	 * 
	 * @param strs
	 */
	public void setCmdCompletors(String[] strs);
}
