package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.util.Map.Entry;

import com.chenjw.knife.core.ClosePacket;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.TextPacket;

public class Agent {
	private static AgentInfo info = null;

	public static void clear() {
		for (Entry<Class<?>, byte[]> entry : info.getBaseMap().entrySet()) {
			try {
				info.getInst().redefineClasses(
						new ClassDefinition(entry.getKey(), entry.getValue()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// send(new TextPacket(entry.getKey().getName() + " recovered!"));
		}
		info.getBaseMap().clear();
	}

	/**
	 * 保存原始类
	 * 
	 * @param clazz
	 */
	private static void backup(Class<?> clazz) {
		byte[] base = info.getBaseMap().get(clazz);
		if (base == null) {
			byte[] bytes = NativeHelper.getClassBytes(clazz);
			info.getBaseMap().put(clazz, bytes);
		}
	}

	public static void redefineClass(Class<?> clazz, byte[] bytecode) {
		try {

			backup(clazz);
			// send(new TextPacket(clazz.getName() + " redefining..."));
			info.getInst()
					.redefineClasses(new ClassDefinition(clazz, bytecode));
			// send(new TextPacket(clazz.getName() + " redefined!"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Class<?>[] getAllLoadedClasses() {
		try {
			return info.getInst().getAllLoadedClasses();
			// inst.addTransformer(t, false);
		} catch (Exception e) {
			e.printStackTrace();
			return new Class<?>[0];
		}
	}

	public static void send(Packet command) {
		try {
			PacketResolver.write(command, info.getOs());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void println(String msg) {
		send(new TextPacket(msg));
	}

	public static void close() {
		try {
			send(new ClosePacket());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			info.getSocket().close();
		} catch (IOException e) {
		}
		info = null;
	}

	public static void setInfo(AgentInfo info) {
		Agent.info = info;
	}

}
