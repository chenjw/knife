package com.chenjw.knife.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.chenjw.knife.core.ObjectSerializer;

public class JavaObjectSerializer implements ObjectSerializer {

	@Override
	public Object toObject(byte[] bytes) {
		if (bytes.length == 0) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public byte[] fromObject(Object obj) {
		if (obj == null) {
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e) {
		}
		return baos.toByteArray();
	}

}
