package com.chenjw.knife.core.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.chenjw.knife.core.Packet;

public abstract class ObjectPacket<T extends Serializable> implements Packet {

	private T object;

	public ObjectPacket(T object) {
		this.object = object;
	}

	public ObjectPacket() {
	}

	@Override
	public byte[] toBytes() {
		if (object == null) {
			return new byte[0];
		}
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
		if (bytes.length == 0) {
			this.object = null;
			return;
		}
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

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

}
