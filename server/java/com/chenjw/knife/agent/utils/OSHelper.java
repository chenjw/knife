package com.chenjw.knife.agent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.utils.info.ThreadInfo;
import com.chenjw.knife.utils.JvmHelper;
import com.chenjw.knife.utils.StringHelper;

public class OSHelper {

	public static List<ThreadInfo> findTopThread(int num) {
		String pid = JvmHelper.getPID();
		try {
			List<ThreadInfo> threadInfos = execTop(pid);
			Map<String, ThreadInfo> threadInfoMap = new HashMap<String, ThreadInfo>();
			for (ThreadInfo threadInfo : threadInfos) {

				threadInfoMap.put(threadInfo.getPid(), threadInfo);
			}
			threadDump(pid, threadInfoMap);
			List<ThreadInfo> result = new ArrayList<ThreadInfo>();
			for (ThreadInfo threadInfo : threadInfos) {
				// System.out.println(threadInfo.getName());
				if (threadInfo.getName() != null) {

					result.add(threadInfo);
				}
			}
			return result;
		} catch (Exception e) {
			return null;
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
			for (int i = 0; i < ts.length; i++) {
				if ("%CPU".equals(ts[i])) {
					cpuIndex = i;
				} else if ("PID".equals(ts[i])) {
					pidIndex = i;
				}
			}
			for (String threadLine : threadLines) {
				threadLine = threadLine.replace("  ", " ").trim();
				String[] values = threadLine.split(" ");

				String id = values[pidIndex];
				if (pid.equals(id)) {
					continue;
				}
				String cpu = values[cpuIndex];
				ThreadInfo threadInfo = new ThreadInfo();
				threadInfo.setCpu(cpu);
				threadInfo.setPid(id);
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

	public static void main(String[] args) throws InterruptedException {

		System.out.println(Integer.parseInt("628688", 16));
		System.out.println("finished!");
	}
}
