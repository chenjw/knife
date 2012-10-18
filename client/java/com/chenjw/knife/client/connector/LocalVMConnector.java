package com.chenjw.knife.client.connector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.client.connection.DefaultVMConnection;
import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.model.VMDescriptor;
import com.chenjw.knife.utils.FileHelper;
import com.chenjw.knife.utils.JarHelper;
import com.chenjw.knife.utils.JvmHelper;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * 本地jvm连接
 * 
 * @author chenjw
 * 
 */
public class LocalVMConnector implements VMConnector {

	private static final String[] exceptJars = new String[] { "jline-1.0.jar",
			"knife-client.jar" };

	public List<VMDescriptor> listVM() throws Exception {
		List<VirtualMachineDescriptor> list = VirtualMachine.list();
		List<VMDescriptor> vmList = new ArrayList<VMDescriptor>();
		String selfJvmId = JvmHelper.getPID();
		for (VirtualMachineDescriptor vm : list) {
			if (vm.id().equals(selfJvmId)) {
				continue;
			}
			VMDescriptor vmd = new VMDescriptor();
			vmd.setId(vm.id());
			vmd.setName(vm.displayName());
			vmList.add(vmd);
		}
		return vmList;
	}

	@Override
	public void attachVM(String pid, int port) throws Exception {
		VirtualMachine vm = VirtualMachine.attach(pid);
		String agentArgs = "port=" + port;
		agentArgs += "&bootstrapJars=" + appendBootstrapJars();
		agentArgs += "&systemJars=" + appendSystemJars();
		vm.loadAgent(JarHelper.findJar("knife-agent.jar"),
				createArgFile(agentArgs));

	}

	public VMConnection createVMConnection(int port) throws Exception {
		return new DefaultVMConnection("127.0.0.1", port);
	}

	private String appendBootstrapJars() {
		return "";
	}

	private String appendSystemJars() {
		String jars = JarHelper.getToolsJarPath();
		for (String jar : JarHelper.findJars()) {
			if (jars.length() != 0) {
				if (!isExceptJar(jar)) {
					jars += ";" + jar;
				}
			}
		}
		return jars;
	}

	private boolean isExceptJar(String str) {
		for (String exceptJar : exceptJars) {
			if (str.indexOf(exceptJar) != -1) {
				return true;
			}
		}
		return false;
	}

	private String createArgFile(String str) {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("agentArgs", ".dat");
			FileHelper.writeStringToFile(tmpFile, str, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmpFile.deleteOnExit();
		return tmpFile.getAbsolutePath();
	}

}
