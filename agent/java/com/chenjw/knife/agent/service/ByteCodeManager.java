package com.chenjw.knife.agent.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.chenjw.knife.agent.util.NativeHelper;

public class ByteCodeManager implements Lifecycle {
	private static final ByteCodeManager INSTANCE = new ByteCodeManager();
	static {
		ServiceManager.getInstance().register(INSTANCE);
	}
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
		// Agent.println("redefine " + clazz + "(" + bytes.length + ")");
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
			byte[] bb = backupMap.get(clazz);
			saveOrignFile(clazz, bb);
			saveNewFile(clazz, defineingBytes);
			// System.err.println(clazz.getName());
			NativeHelper.redefineClass(clazz, defineingBytes);
			definedMap.put(clazz, defineingBytes);
			defineingMap.remove(clazz);

			// Agent.println("commited " + clazz.getName() + "("
			// + defineingBytes.length + ")(" + bb.length + ")");
		}
		// System.out.println("redefine " + clazz + "(" + defineingBytes.length
		// + ")");
	}

	private void saveOrignFile(Class<?> clazz, byte[] byteCode) {
		String path = "/tmp/knife/orign/"
				+ StringUtils
						.replaceChars(clazz.getName(), ".", File.separator)
				+ ".class";
		try {
			FileUtils.writeByteArrayToFile(new File(path), byteCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveNewFile(Class<?> clazz, byte[] byteCode) {
		String path = "/tmp/knife/new/"
				+ StringUtils
						.replaceChars(clazz.getName(), ".", File.separator)
				+ ".class";
		clazz.getName().replaceAll(".", File.separator);
		try {
			FileUtils.writeByteArrayToFile(new File(path), byteCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	public void init() {

	}

	@Override
	public void clear() {
		recoverAll();
	}
}
