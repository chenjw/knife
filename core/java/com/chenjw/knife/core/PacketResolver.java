package com.chenjw.knife.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 格式
 * 
 * magic(5位) 类型长度(8位) 类型 内容长度(8位) 内容
 * 
 * 
 * @author chenjw
 * 
 */
public class PacketResolver {

	private static final String MAGIC = "KNIFE";
	private static final int MAGIC_LENGTH = MAGIC.getBytes().length;

	private static Object netInstance(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	public static Packet read(InputStream is) throws IOException {
		Packet packet = null;
		checkMagic(is);
		int typeLength = (int) readLong(is);
		String type = readType(is, typeLength);
		packet = initPacket(type);
		int contentLength = (int) readLong(is);
		byte[] bytes = null;
		if (contentLength == 0) {
			bytes = new byte[0];
		} else {
			bytes = new byte[contentLength];
			is.read(bytes, 0, contentLength);
		}
		packet.fromBytes(bytes);
		return packet;
	}

	public static void write(Packet packet, OutputStream os) throws IOException {
		os.write(MAGIC.getBytes());
		byte[] type = packet.getClass().getName().getBytes();
		os.write(long2bytes(type.length));
		os.write(type);
		byte[] content = packet.toBytes();
		os.write(long2bytes(content.length));
		if (content.length > 0) {
			os.write(content);
		}
	}

	private static long readLong(InputStream is) throws IOException {
		byte[] bytes = new byte[8];
		int size = is.read(bytes);
		if (size == -1) {
			throw new IOException("read -1");
		}
		return bytes2long(bytes);
	}

	private static void checkMagic(InputStream is) throws IOException {
		byte[] bytes = new byte[MAGIC_LENGTH];
		int size = is.read(bytes);
		if (size == -1) {
			throw new IOException("read -1");
		}
		if (!MAGIC.equals(new String(bytes))) {
			throw new IOException("MAGIC check fail (" + new String(bytes)
					+ ")");
		}
	}

	private static Packet initPacket(String type) throws IOException {
		try {
			Class<?> clazz = Class.forName(type);
			return (Packet) netInstance(clazz);
		} catch (ClassNotFoundException e) {
			throw new IOException("type not found (" + type + ")");
		}
	}

	private static String readType(InputStream is, int length)
			throws IOException {
		byte[] bytes = new byte[length];
		int size = is.read(bytes);
		if (size == -1) {
			throw new IOException("read -1");
		}
		return new String(bytes);
	}

	public static long bytes2long(byte[] b) {

		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 8; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	public static byte[] long2bytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		return b;
	}
}
