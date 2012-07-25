package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.util.jar.JarFile;

import com.chenjw.knife.agent.handler.log.ByteCodeManager;
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
		// for (Entry<Class<?>, byte[]> entry : info.getBaseMap().entrySet()) {
		// try {
		// NativeHelper.redefineClass(entry.getKey(), entry.getValue());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// // send(new TextPacket(entry.getKey().getName() + " recovered!"));
		// }
		// info.getBaseMap().clear();
		ByteCodeManager.getInstance().recoverAll();
	}

	/**
	 * 保存原始类
	 * 
	 * @param clazz
	 */
	private static void backup(Class<?> clazz) {
		// byte[] base = info.getBaseMap().get(clazz);
		// if (base == null) {
		// byte[] bytes = NativeHelper.getClassBytes(clazz);
		// info.getBaseMap().put(clazz, bytes);
		// }
	}

	private static void redefineClass(Class<?> clazz, byte[] bytecode) {
		try {

			backup(clazz);
			// System.out.println(clazz.getName() + "(" + bytecode.length
			// + ") redefining...");
			info.getInst()
					.redefineClasses(new ClassDefinition(clazz, bytecode));
			// NativeHelper.redefineClass(clazz, bytecode);
			// System.out.println(clazz.getName() + " redefined!");

			// FileUtils.writeByteArrayToFile(new File("/home/chenjw/test/"
			// + clazz.getName() + ".class"),
			// NativeHelper.getClassBytes(clazz));
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
