package com.chenjw.knife.core.serializer;

/**
 * 用于序列化反序列化对象。
 * 
 * @author chenjw
 * 
 */
public interface ObjectSerializer {
	public Object toObject(byte[] bytes);

	public byte[] fromObject(Object obj);
}
