package com.chenjw.knife.core.serializer;

public interface ObjectSerializer {
	public Object toObject(byte[] bytes);

	public byte[] fromObject(Object obj);
}
