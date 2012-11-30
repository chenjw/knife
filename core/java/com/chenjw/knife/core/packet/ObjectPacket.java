package com.chenjw.knife.core.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import com.chenjw.knife.core.Packet;

public abstract class ObjectPacket<T extends Serializable> implements Packet {


	private static final long serialVersionUID = 6382032545189938912L;
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
			oos = new ObjectOutputStream(baos){

				@Override
				protected void writeStreamHeader() throws IOException {
					
				}
				
			};
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
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
			ois = new ObjectInputStream(bais){

				@Override
				protected void readStreamHeader() throws IOException,
						StreamCorruptedException {
					
				}
				
			};
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
