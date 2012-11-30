package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.packet.ClosePacket;
import com.chenjw.knife.core.packet.ResultPacket;
import com.chenjw.knife.core.packet.TextPacket;
import com.chenjw.knife.core.result.Result;

public class Agent {
	private static AgentInfo agentInfo = null;

	public static Printer printer = new Printer() {
		@Override
		public void info(String str) {
			Agent.info(str);
		}

		@Override
		public void debug(String str) {
			Agent.debug(str);
		}
	};

	public static void redefineClasses(Class<?> clazz, byte[] bytes) {
		try {
			agentInfo.getInst().redefineClasses(
					new ClassDefinition(clazz, bytes));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmodifiableClassException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Class<?>[] getAllLoadedClasses() {
		try {
			return agentInfo.getInst().getAllLoadedClasses();
			// inst.addTransformer(t, false);
		} catch (Exception e) {
			e.printStackTrace();
			return new Class<?>[0];
		}
	}

	public static void sendResult(Result<?> r) {
		send(new ResultPacket(r));
	}

	public static void send(Packet command) {
		try {
			PacketResolver.write(command, agentInfo.getOs());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isDebugOn() {
		if (agentInfo == null) {
			return false;
		} else {
			return agentInfo.isDebugOn();
		}

	}

	public static void info(String msg) {
		send(new TextPacket(msg));
	}

	public static void debug(String msg) {
		if (isDebugOn()) {
			info("[DEBUG] " + msg);
		}
	}

	public static void close() {
		try {
			send(new ClosePacket());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			agentInfo.getSocket().close();
		} catch (Throwable e) {
		}
		agentInfo = null;
	}

	public static void setAgentInfo(AgentInfo info) {
		Agent.agentInfo = info;
	}

	public static AgentInfo getAgentInfo() {
		return agentInfo;
	}

}
