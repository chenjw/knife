package com.chenjw.knife.core.packet;

import java.io.Serializable;
import java.util.ServiceLoader;

import com.chenjw.knife.core.ObjectSerializer;
import com.chenjw.knife.core.Packet;

public abstract class ObjectPacket<T extends Serializable> implements Packet {

	private static final long serialVersionUID = 6382032545189938912L;
	private T object;
	private static ObjectSerializer objectSerializer = ServiceLoader
			.load(ObjectSerializer.class, ObjectPacket.class.getClassLoader())
			.iterator().next();

	public ObjectPacket(T object) {
		this.object = object;
	}

	public ObjectPacket() {
	}

	@Override
	public byte[] toBytes() {
		return objectSerializer.fromObject(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(byte[] bytes) {
		this.object = (T) objectSerializer.toObject(bytes);
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

}
