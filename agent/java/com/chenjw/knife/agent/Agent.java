package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

import com.chenjw.knife.agent.handler.log.InvokeRecord;
import com.chenjw.knife.agent.handler.log.TraceCodeBuilder;
import com.chenjw.knife.core.ClosePacket;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.TextPacket;

public class Agent {
	private static AgentInfo info = null;
	public static Printer printer = new Printer() {
		@Override
		public void println(String str) {
			Agent.println(str);
		}

	};

	public static void redefineClasses(Class<?> clazz, byte[] bytes) {
		try {
			info.getInst().redefineClasses(new ClassDefinition(clazz, bytes));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmodifiableClassException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void clear() {
		InvokeRecord.clear();
		TraceCodeBuilder.clear();
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
		} catch (Throwable e) {
		}
		info = null;
	}

	public static void setInfo(AgentInfo info) {
		Agent.info = info;
	}

}
