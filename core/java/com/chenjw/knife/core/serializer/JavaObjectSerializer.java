package com.chenjw.knife.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * java原生实现的序列化，如果系列化机和反序列化机的jdk版本不一致，有可能会解析失败,所以建议使用json方式更好.
 * 
 * @author chenjw
 * 
 */
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
