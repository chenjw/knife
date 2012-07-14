package com.chenjw.knife.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class PacketResolver {
	private static Map<Byte, Class<? extends Packet>> packetMap = new HashMap<Byte, Class<? extends Packet>>();;

	static {
		packetMap.put(TextPacket.CODE, TextPacket.class);
		packetMap.put(ObjectPacket.CODE, ObjectPacket.class);
		packetMap.put(ClosePacket.CODE, ClosePacket.class);
	}

	private static Packet netInstance(Class<? extends Packet> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	public static Packet read(InputStream is) throws IOException {
		Packet packet = null;
		byte code = readCode(is);
		int size = (int) readLong(is);
		byte[] bytes = new byte[size];
		is.read(bytes, 0, size);
		packet = netInstance(packetMap.get(code));
		packet.fromBytes(bytes);
		return packet;
	}

	public static void write(Packet packet, OutputStream os)
			throws IOException {
		os.write(new byte[] { packet.getCode() });
		byte[] bytes = packet.toBytes();
		os.write(long2bytes(bytes.length));
		os.write(bytes);
	}

	private static long readLong(InputStream is) throws IOException {
		byte[] bytes = new byte[8];
		int size = is.read(bytes);
		if (size == -1) {
			throw new IOException("read -1");
		}
		return bytes2long(bytes);
	}

	private static byte readCode(InputStream is) throws IOException {
		byte[] b = new byte[1];
		int i = is.read(b);
		if (i != -1) {
			return b[0];
		} else {
			throw new IOException();
		}
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		send(new TextPacket("aaa"));
		// send(new ObjectCommand(new NewObject()));
	}

	private static void send(Packet packet) throws IOException {
		Socket socket = new Socket("127.0.0.1", 1111);
		OutputStream os = socket.getOutputStream();
		PacketResolver.write(packet, os);
		socket.close();
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
