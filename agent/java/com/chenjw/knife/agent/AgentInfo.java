package com.chenjw.knife.agent;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentInfo {
	private Socket socket;
	private OutputStream os;
	private Instrumentation inst;
	private Map<Class<?>, byte[]> baseMap = new ConcurrentHashMap<Class<?>, byte[]>();
	// will be loaded when enter and unload when close
	private List<String> bootstrapJars;
	private List<String> systemJars;

	public AgentInfo() {
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

	public List<String> getBootstrapJars() {
		return bootstrapJars;
	}

	public List<String> getSystemJars() {
		return systemJars;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setInst(Instrumentation inst) {
		this.inst = inst;
	}

	public void setBootstrapJars(List<String> bootstrapJars) {
		this.bootstrapJars = bootstrapJars;
	}

	public void setSystemJars(List<String> systemJars) {
		this.systemJars = systemJars;
	}

}
