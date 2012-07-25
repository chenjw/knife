package com.chenjw.knife.agent.handler.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chenjw.knife.agent.NativeHelper;

public class ByteCodeManager {
	private static final ByteCodeManager INSTANCE = new ByteCodeManager();

	private Map<Class<?>, byte[]> backupMap = new ConcurrentHashMap<Class<?>, byte[]>();

	private Map<Class<?>, byte[]> defineingMap = new ConcurrentHashMap<Class<?>, byte[]>();

	private Map<Class<?>, byte[]> definedMap = new ConcurrentHashMap<Class<?>, byte[]>();

	public static ByteCodeManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 保存原始类
	 * 
	 * @param clazz
	 */
	private void load(Class<?> clazz) {
		byte[] base = backupMap.get(clazz);
		if (base == null) {
			byte[] bytes = NativeHelper.getClassBytes(clazz);
			backupMap.put(clazz, bytes);
			definedMap.put(clazz, bytes);
		}
	}

	public byte[] getByteCode(Class<?> clazz) {
		byte[] defineingBytes = defineingMap.get(clazz);
		if (defineingBytes != null) {
			// System.out.println("getByteCode " + clazz + "("
			// + defineingBytes.length + ")");
			return defineingBytes;
		} else {
			load(clazz);
			byte[] defined = definedMap.get(clazz);
			// System.out.println("getByteCode " + clazz + "(" + defined.length
			// + ")");
			return defined;
		}
	}

	/**
	 * try to redefine class , not commit to jvm
	 * 
	 * @param clazz
	 * @param bytes
	 */
	public void tryRedefineClass(Class<?> clazz, byte[] bytes) {
		load(clazz);
		defineingMap.put(clazz, bytes);
		// System.out.println("redefine " + clazz + "(" + bytes.length + ")");
	}

	public void commitAll() {
		for (Class<?> clazz : defineingMap.keySet()) {
			commit(clazz);
		}
	}

	/**
	 * commit the redefined class to jvm
	 * 
	 * @param clazz
	 */
	public void commit(Class<?> clazz) {
		byte[] defineingBytes = defineingMap.get(clazz);
		if (defineingBytes != null) {
			NativeHelper.redefineClass(clazz, defineingBytes);
			definedMap.put(clazz, defineingBytes);
			defineingMap.remove(clazz);
		}
		// System.out.println("redefine " + clazz + "(" + defineingBytes.length
		// + ")");
	}

	public void rollbackAll() {
		for (Class<?> clazz : defineingMap.keySet()) {
			rollback(clazz);
		}
	}

	public void rollback(Class<?> clazz) {
		definedMap.remove(clazz);
	}

	public void recoverAll() {
		for (Class<?> clazz : backupMap.keySet()) {
			recover(clazz);
		}
	}

	public void recover(Class<?> clazz) {
		byte[] backupBytes = backupMap.get(clazz);
		if (backupBytes != null) {
			byte[] definedBytes = definedMap.get(clazz);
			if (definedBytes != backupBytes) {
				NativeHelper.redefineClass(clazz, backupBytes);
			}
			definedMap.remove(clazz);
			defineingMap.remove(clazz);
			backupMap.remove(clazz);
		}
	}

}
