package com.chenjw.knife.client.connector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.client.connection.DefaultVMConnection;
import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.core.model.VMDescriptor;
import com.chenjw.knife.utils.FileHelper;
import com.chenjw.knife.utils.JarHelper;
import com.chenjw.knife.utils.JvmHelper;
import com.chenjw.knife.utils.LinuxHelper;
import com.chenjw.knife.utils.PlatformHelper;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * 本地jvm连接
 * 
 * @author chenjw
 * 
 */
public class LocalVMConnector implements VMConnector {

	private static final String[] agentJars = new String[] {
			"fastjson-1.1.17.jar", "misc.javassist-3.9.0.GA.jar",
			"knife-server.jar" };

	public List<VMDescriptor> listVM() throws Exception {

//		if (PlatformHelper.isLinux()) {
//			List<VMDescriptor> vmList = new ArrayList<VMDescriptor>();
//			String selfJvmId = JvmHelper.getPID();
//			for( VMDescriptor vm:LinuxHelper.listVM()){
//				if (vm.getPid().equals(selfJvmId)) {
//					continue;
//				}
//				vmList.add(vm);
//			}
//			return vmList;
//		}
//		else{
			List<VirtualMachineDescriptor> list = VirtualMachine.list();
			List<VMDescriptor> vmList = new ArrayList<VMDescriptor>();
			String selfJvmId = JvmHelper.getPID();
			for (VirtualMachineDescriptor vm : list) {
				if (vm.id().equals(selfJvmId)) {
					continue;
				}
				VMDescriptor vmd = new VMDescriptor();
				vmd.setPId(vm.id());
				vmd.setName(vm.displayName());
				vmList.add(vmd);
			}
			return vmList;
//		}
		
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
				if (isAgentJar(jar)) {
					jars += ";" + jar;
				}
			}
		}
		return jars;
	}

	private boolean isAgentJar(String str) {
		for (String agentJar : agentJars) {
			if (str.indexOf(agentJar) != -1) {
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
