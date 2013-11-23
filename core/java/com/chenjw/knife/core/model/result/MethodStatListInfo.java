package com.chenjw.knife.core.model.result;

import java.io.Serializable;

/**
 * 方法统计列表
 * 
 * @author chenjw
 * @version $Id: MethodStatListInfo.java, v 0.1 2013年11月23日 下午2:43:13 chenjw Exp $
 */
public class MethodStatListInfo implements Serializable {

	private static final long serialVersionUID = -2538967520334519918L;
	/** 方法统计列表 */
	private MethodStatInfo[] methodStatInfos;
	
    public MethodStatInfo[] getMethodStatInfos() {
        return methodStatInfos;
    }
    public void setMethodStatInfos(MethodStatInfo[] methodStatInfos) {
        this.methodStatInfos = methodStatInfos;
    }

	
}
