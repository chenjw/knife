package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

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

	/**
	 * install jars dependent by agent
	 * 
	 * @param inst
	 * @throws IOException
	 */
	public static void installJars() throws IOException {
		if (info.getBootstrapJars() != null) {
			for (String path : info.getBootstrapJars()) {
				info.getInst().appendToBootstrapClassLoaderSearch(
						new JarFile(path));

			}
		}

		if (info.getSystemJars() != null) {
			for (String path : info.getSystemJars()) {
				info.getInst().appendToSystemClassLoaderSearch(
						new JarFile(path));
			}
		}

	}

	/**
	 * uninstall jars dependent by agent
	 */
	public static void uninstallJars() {

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
