package com.chenjw.knife.client.core;

import java.io.IOException;
import java.util.List;

import com.chenjw.knife.core.model.VMDescriptor;

public interface VMConnector {

	/**
	 * 获得虚拟机列表
	 * 
	 * @throws IOException
	 */
	public List<VMDescriptor> listVM() throws Exception;

	/**
	 * attach到虚拟机
	 * 
	 * @param num
	 *            listVM方法返回的虚拟机列表中的序号
	 * @throws IOException
	 */
	public void attachVM(String id, int port) throws Exception;

	public VMConnection createVMConnection(int port) throws Exception;

}
