package com.chenjw.knife.client.attach.provider;

import com.chenjw.knife.client.attach.VirtualMachineDescriptor;

/*
 * Platform specific provider implementations extend this
 */
public class RemoteAttachProvider extends AbstractAttachProvider {

	// perf count name for the JVM version
	private static final String JVM_VERSION = "java.property.java.vm.version";
	private String ip;
	private int port;

	public RemoteAttachProvider(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public static void main(String[] args) {
		RemoteAttachProvider p = new RemoteAttachProvider("127.0.0.1", 1099);
		for (VirtualMachineDescriptor d : p.listVirtualMachines()) {
			System.out.println(d.id() + ". " + d.displayName());
		}

	}

	@Override
	protected String getUrl() {
		return "//" + ip + ":" + port;
	}

}