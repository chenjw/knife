package com.chenjw.knife.agent.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.utils.FileHelper;
import com.chenjw.knife.utils.StringHelper;

public class ByteCodeService implements Lifecycle {

	private Map<Class<?>, byte[]> backupMap = new ConcurrentHashMap<Class<?>, byte[]>();

	private Map<Class<?>, byte[]> defineingMap = new ConcurrentHashMap<Class<?>, byte[]>();

	private Map<Class<?>, byte[]> definedMap = new ConcurrentHashMap<Class<?>, byte[]>();

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
			saveOrignFile(clazz, bytes);
			Agent.debug("[ByteCodeManager] backup " + clazz + "("
					+ bytes.length + ")");
		}
	}

	public byte[] getByteCode(Class<?> clazz) {
		byte[] defineingBytes = defineingMap.get(clazz);
		if (defineingBytes != null) {
			Agent.debug("[ByteCodeManager] getDefineingByteCode " + clazz + "("
					+ defineingBytes.length + ")");
			return defineingBytes;
		} else {
			load(clazz);
			byte[] defined = definedMap.get(clazz);
			Agent.debug("[ByteCodeManager] getNewDefineByteCode " + clazz + "("
					+ defined.length + ")");
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
		Agent.debug("[ByteCodeManager] tryRedefineClass " + clazz + "("
				+ bytes.length + ")");
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

			saveNewFile(clazz, defineingBytes);
			NativeHelper.redefineClass(clazz, defineingBytes);
			definedMap.put(clazz, defineingBytes);
			defineingMap.remove(clazz);

			Agent.debug("[ByteCodeManager] commited " + clazz.getName() + "("
					+ defineingBytes.length + ")(" + bb.length + ")");
		}
	}

	private void saveOrignFile(Class<?> clazz, byte[] byteCode) {
		String path = "/tmp/knife/orign/"
				+ StringHelper.replaceChars(clazz.getName(), ".",
						File.separator) + ".class";
		try {
			FileHelper.writeByteArrayToFile(new File(path), byteCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveNewFile(Class<?> clazz, byte[] byteCode) {
		String path = "/tmp/knife/new/"
				+ StringHelper.replaceChars(clazz.getName(), ".",
						File.separator) + ".class";
		clazz.getName().replaceAll(".", File.separator);
		try {
			FileHelper.writeByteArrayToFile(new File(path), byteCode);
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
		Agent.debug("[ByteCodeManager] rollback " + clazz.getName());
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

	@Override
	public void close() {

	}
}
