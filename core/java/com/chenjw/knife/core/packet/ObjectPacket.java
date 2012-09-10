package com.chenjw.knife.core.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketResolver;

public class ObjectPacket<T> implements Packet {
	static final byte CODE = 1;
	static {
		PacketResolver.register(CODE, ObjectPacket.class);
	}
	private T object;

	public ObjectPacket(T object) {
		this.object = object;
	}

	public ObjectPacket() {
	}

	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
		}
		return baos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			this.object = (T) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte getCode() {
		return CODE;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

}
