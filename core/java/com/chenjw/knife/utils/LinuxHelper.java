package com.chenjw.knife.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.core.model.VMDescriptor;
import com.chenjw.knife.core.model.result.ThreadInfo;

public class LinuxHelper {



	public static List<ThreadInfo> findTopThread(int num) {
		String pid = JvmHelper.getPID();
		try {
			List<ThreadInfo> threadInfos = execTop(pid);
			Map<String, ThreadInfo> threadInfoMap = new HashMap<String, ThreadInfo>();
			for (ThreadInfo threadInfo : threadInfos) {

				threadInfoMap.put(threadInfo.getTid(), threadInfo);
			}
			threadDump(pid, threadInfoMap);
			List<ThreadInfo> result = new ArrayList<ThreadInfo>();
			int n = 0;
			for (ThreadInfo threadInfo : threadInfos) {
				if (n > num) {
					break;
				}
				// System.out.println(threadInfo.getName());
				if (threadInfo.getName() != null) {
					result.add(threadInfo);
					n++;
				}
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) throws Exception{
		List<VMDescriptor> vms=listVM();
		for(VMDescriptor vm:vms){
			System.out.println(vm.getPid()+" | "+vm.getName());
		}
	}
	
	public static List<VMDescriptor> listVM() throws Exception {
		List<VMDescriptor> result = new ArrayList<VMDescriptor>();
		String command = "ps ax";
		InputStream is = null;
		Process process = Runtime.getRuntime().exec(command);
		try {
			is = process.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = input.readLine()) != null) {
				VMDescriptor vm=new VMDescriptor();
				String[] ss=line.split(" ");
				int index=0;
				for(int i=0;i<ss.length;i++){
					String s=ss[i];
					if(StringHelper.isBlank(s)){
						continue;
					}
					else{
						if(index==0){
							vm.setPId(s);
						}
						else if(index==4){
							String[] nameA=Arrays.copyOfRange(ss, i, ss.length);
							vm.setName(StringHelper.join(nameA , " "));
						}
						index++;
					}
				}
				String cmd=StringHelper.substringBefore(vm.getName(), " ");
				if("java".equals(cmd)||cmd.endsWith("/java")){
					result.add(vm);
				}
			}
			return result;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			process.destroy();
		}
	}

	private static List<ThreadInfo> execTop(String pid) throws Exception {
		List<ThreadInfo> result = new ArrayList<ThreadInfo>();
		String command = "top -p " + pid + " -H -b -n 1";
		InputStream is = null;
		Process process = Runtime.getRuntime().exec(command);
		try {

			is = process.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			String title = null;
			List<String> threadLines = new ArrayList<String>();
			String line;
			int status = 1;
			while ((line = input.readLine()) != null) {
				if (status == 1) {
					if (StringHelper.isBlank(line)) {
						status = 2;
					}
				} else if (status == 2) {
					title = line;
					status = 3;
				} else if (status == 3) {
					if (StringHelper.isBlank(line)) {
						break;
					} else {
						threadLines.add(line);
					}
				}
			}
			int pidIndex = 0;
			int cpuIndex = 0;
			title = title.replace("  ", " ").trim();
			String[] ts = title.split(" ");
			int i = 0;
			for (String tsi : ts) {
				if (StringHelper.isBlank(tsi)) {
					continue;
				}
				tsi = tsi.trim();

				if ("%CPU".equals(tsi)) {
					cpuIndex = i;
				} else if ("PID".equals(tsi)) {
					pidIndex = i;
				}
				i++;
			}
			for (String threadLine : threadLines) {
				threadLine = threadLine.replace("  ", " ").trim();
				String[] valueSplit = threadLine.split(" ");
				List<String> valueList = new ArrayList<String>();
				for (String v : valueSplit) {
					if (StringHelper.isBlank(v)) {
						continue;
					}
					valueList.add(v.trim());
				}
				String id = valueList.get(pidIndex);
				if (pid.equals(id)) {
					continue;
				}
				String cpu = valueList.get(cpuIndex);
				ThreadInfo threadInfo = new ThreadInfo();
				threadInfo.setCpu(cpu);
				threadInfo.setTid(id);
				result.add(threadInfo);
			}
			return result;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			process.destroy();
		}
	}

	private static void threadDump(String pid,
			Map<String, ThreadInfo> threadInfoMap) throws Exception {
		String command = "jstack " + pid;
		InputStream is = null;
		Process process = Runtime.getRuntime().exec(command);
		try {

			is = process.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = input.readLine()) != null) {
				if (line.startsWith("\"")) {
					String name = line.substring(1, line.indexOf("\"", 1));
					String[] strs = line.split(" ");
					String id = null;
					for (String str : strs) {
						if (str.startsWith("nid=0x")) {
							String id16 = StringHelper.substringAfter(str,
									"nid=0x");
							id = String.valueOf(Integer.parseInt(id16, 16));
						}
					}
					ThreadInfo threadInfo = threadInfoMap.get(id);
					if (threadInfo == null) {
						continue;
					}
					threadInfo.setName(name);
				}
			}
			// return result;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			process.destroy();
		}
	}

}
