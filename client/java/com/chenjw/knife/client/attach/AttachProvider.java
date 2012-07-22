package com.chenjw.knife.client.attach;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface AttachProvider {

	public abstract String name();

	public abstract String type();

	public VirtualMachine attachVirtualMachine(VirtualMachineDescriptor vmd);

	public abstract List<VirtualMachineDescriptor> listVirtualMachines();

	public InputStream execute(String cmd, Object... args) throws IOException;

}