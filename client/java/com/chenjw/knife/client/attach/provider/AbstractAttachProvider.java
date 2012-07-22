package com.chenjw.knife.client.attach.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import com.chenjw.knife.client.attach.AttachProvider;
import com.chenjw.knife.client.attach.VirtualMachine;
import com.chenjw.knife.client.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.AttachNotSupportedException;

public abstract class AbstractAttachProvider implements AttachProvider {
	private static final String PROTOCOL_VERSION = "1";
	private static final int ATTACH_ERROR_BADVERSION = 101;

	protected abstract String getUrl();

	public List<VirtualMachineDescriptor> listVirtualMachines() {
		ArrayList<VirtualMachineDescriptor> result = new ArrayList<VirtualMachineDescriptor>();

		MonitoredHost host;
		Set vms;
		try {
			host = MonitoredHost.getMonitoredHost(new HostIdentifier(getUrl()));
			vms = host.activeVms();
		} catch (Throwable t) {
			t.printStackTrace();
			throw new InternalError(); // shouldn't happen
		}

		for (Object vmid : vms) {
			if (vmid instanceof Integer) {
				String pid = vmid.toString();
				String name = pid; // default to pid if name not available
				boolean isAttachable = false;
				MonitoredVm mvm = null;
				try {
					mvm = host.getMonitoredVm(new VmIdentifier(pid));
					try {
						isAttachable = MonitoredVmUtil.isAttachable(mvm);
						// use the command line as the display name
						name = MonitoredVmUtil.commandLine(mvm);
					} catch (Exception e) {
					}
					if (isAttachable) {
						result.add(new VirtualMachineDescriptor(this, pid, name));
					}
				} catch (Throwable t) {
					if (t instanceof ThreadDeath) {
						throw (ThreadDeath) t;
					}
				} finally {
					if (mvm != null) {
						mvm.detach();
					}
				}
			}
		}
		return result;
	}

	void testAttachable(String id) throws AttachNotSupportedException {
		MonitoredVm mvm = null;
		boolean isKernelVM = false;
		try {
			VmIdentifier vmid = new VmIdentifier(id);
			MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
			mvm = host.getMonitoredVm(vmid);

			if (MonitoredVmUtil.isAttachable(mvm)) {
				// it's attachable; so return false
				return;
			}
			isKernelVM = MonitoredVmUtil.isKernelVM(mvm);
		} catch (Throwable t) {
			if (t instanceof ThreadDeath) {
				ThreadDeath td = (ThreadDeath) t;
				throw td;
			}
			// we do not know what this id is
			return;
		} finally {
			if (mvm != null) {
				mvm.detach();
			}
		}

		// we're sure it's not attachable; throw exception
		if (isKernelVM) {
			throw new AttachNotSupportedException(
					"Kernel VM does not support the attach mechanism");
		} else {
			throw new AttachNotSupportedException(
					"The VM does not support the attach mechanism");
		}
	}

	/**
	 * Execute the given command in the target VM.
	 */
	public InputStream execute(String cmd, Object... args) throws IOException {

		Socket socket = new Socket("127.0.0.1", 1099);
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		writeString(os, PROTOCOL_VERSION);
		writeString(os, cmd);

		for (int i = 0; i < 3; i++) {
			if (i < args.length && args[i] != null) {
				writeString(os, (String) args[i]);
			} else {
				writeString(os, "");
			}
		}
		int completionStatus = is.read();
		if (completionStatus != 0) {
			System.out.println(completionStatus);
			is.close();
			if (completionStatus == ATTACH_ERROR_BADVERSION) {
				throw new IOException("Protocol mismatch with target VM");
			}
			if (cmd.equals("load")) {
				throw new IOException("Failed to load agent library");
			} else {
				throw new IOException("Command failed in target VM");
			}
		}
		return is;
	}

	private void writeString(OutputStream os, String s) throws IOException {
		if (s.length() > 0) {
			byte b[];
			try {
				b = s.getBytes("UTF-8");
			} catch (java.io.UnsupportedEncodingException x) {
				throw new InternalError();
			}
			os.write(b, 0, b.length);
		}
		byte b[] = new byte[1];
		b[0] = 0;
		os.write(b, 0, 1);
	}

	@Override
	public VirtualMachine attachVirtualMachine(VirtualMachineDescriptor vmd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String type() {
		// TODO Auto-generated method stub
		return null;
	}

}
