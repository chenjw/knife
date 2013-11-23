package com.chenjw.knife.core.model.result;

import java.io.Serializable;

/**
 * 方法统计
 * 
 * @author chenjw
 * @version $Id: MethodStatInfo.java, v 0.1 2013年11月23日 下午2:41:44 chenjw Exp $
 */
public class MethodStatInfo implements Serializable {

	/**  */
    private static final long serialVersionUID = -1210901508732074645L;
    private String method;
	private long count;
	
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }

	

}
