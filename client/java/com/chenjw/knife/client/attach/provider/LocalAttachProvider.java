package com.chenjw.knife.client.attach.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import com.chenjw.knife.client.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.AttachNotSupportedException;

/*
 * Platform specific provider implementations extend this
 */
public class LocalAttachProvider extends AbstractAttachProvider {

	public LocalAttachProvider() {
	}

	public static void main(String[] args) {
		LocalAttachProvider p = new LocalAttachProvider();
		for (VirtualMachineDescriptor d : p.listVirtualMachines()) {
			System.out.println(d.id() + ". " + d.displayName());
		}

	}

	public List<VirtualMachineDescriptor> listVirtualMachines() {
		ArrayList<VirtualMachineDescriptor> result = new ArrayList<VirtualMachineDescriptor>();

		MonitoredHost host;
		Set vms;
		try {
			host = MonitoredHost.getMonitoredHost(new HostIdentifier(
					"//127.0.0.1"));
			vms = host.activeVms();
		} catch (Throwable t) {
			if (t instanceof ExceptionInInitializerError) {
				t = t.getCause();
			}
			if (t instanceof ThreadDeath) {
				throw (ThreadDeath) t;
			}
			if (t instanceof SecurityException) {
				return result;
			}
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

	/**
	 * Test if a VM is attachable. If it's not attachable, an
	 * AttachNotSupportedException will be thrown. For example, 1.4.2 or 5.0 VM
	 * are not attachable. There are cases that we can't determine if a VM is
	 * attachable or not and this method will just return.
	 * 
	 * This method uses the jvmstat counter to determine if a VM is attachable.
	 * If the target VM does not have a jvmstat share memory buffer, this method
	 * returns.
	 * 
	 * @exception AttachNotSupportedException
	 *                if it's not attachable
	 */
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

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

}