package com.chenjw.knife.agent;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentInfo {
	private Socket socket;
	private OutputStream os;
	private Instrumentation inst;
	private Map<Class<?>, byte[]> baseMap = new ConcurrentHashMap<Class<?>, byte[]>();

	public AgentInfo(Socket socket, Instrumentation inst) {
		try {
			this.inst = inst;
			this.socket = socket;
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}

	public Socket getSocket() {
		return socket;
	}

	public Instrumentation getInst() {
		return inst;
	}

	public Map<Class<?>, byte[]> getBaseMap() {
		return baseMap;
	}

}
