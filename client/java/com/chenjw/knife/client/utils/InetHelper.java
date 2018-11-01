package com.chenjw.knife.client.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.chenjw.knife.client.utils.ssh.SshClient;
import com.chenjw.knife.utils.StringHelper;
import com.jscape.inet.scp.Scp;
import com.jscape.inet.ssh.Ssh;
import com.jscape.inet.ssh.SshConnectedEvent;
import com.jscape.inet.ssh.SshDataReceivedEvent;
import com.jscape.inet.ssh.SshDisconnectedEvent;
import com.jscape.inet.ssh.SshListener;
import com.jscape.inet.ssh.util.SshParameters;

public class InetHelper {

	public static byte[] scpGet(String hostname, String username,
			String password, String targetFile) {
		SshParameters params = new SshParameters(hostname, username, password);
		Scp scp = new Scp(params);
		byte[] bytes = null;
		try {
			ByteArrayOutputStream f = new ByteArrayOutputStream();
			scp.connect();
			String path = StringHelper.substringBeforeLast(targetFile, "/")
					+ "/";
			String fileName = StringHelper.substringAfterLast(targetFile, "/");
			scp.download(f, path, fileName);
			bytes = f.toByteArray();
		} catch (Exception e) {
			 e.printStackTrace();
		} finally {
			scp.disconnect();
		}
		return bytes;
	}

	public static void scpPut(String hostname, String username,
			String password, String srcPath, String targetPath,
			String targetName) {
		SshParameters params = new SshParameters(hostname, username, password);
		Scp scp = new Scp(params);
		try {
			scp.connect();
			File f = new File(srcPath);
			if (f.isDirectory()) {
				scp.uploadDir(f, targetPath, targetName);
			} else {
				scp.upload(f, targetPath, targetName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		scp.disconnect();
	}

	public static void ssh(String command, String hostname, String username,
			String password) {

		try {
			SshParameters sshParams = new SshParameters(hostname, username,
					password);
			final Ssh ssh = new Ssh(sshParams);
			ssh.setReadTimeout(0);
			ssh.setTimeout(0);
			ssh.connect();
			ssh.requestExec(command);

			ssh.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SshClient ssh(String hostname, String username,
			String password) {

		try {
			SshParameters sshParams = new SshParameters(hostname, username,
					password);
			final StringBuffer sb = new StringBuffer();
			final BlockingQueue<String> lines = new ArrayBlockingQueue<String>(
					1);
			final Ssh ssh = new Ssh(sshParams);
			ssh.addSshListener(new SshListener() {

				@Override
				public void connected(SshConnectedEvent arg0) {

				}

				@Override
				public void dataReceived(SshDataReceivedEvent arg0) {
					String str = arg0.getData();
					if (str != null) {
						for (char c : str.toCharArray()) {
							if (c == '\n') {
								try {
									lines.put(sb.toString());
								} catch (InterruptedException e) {
								  e.printStackTrace();
								}
								sb.delete(0, sb.length());
							} else {
								sb.append(c);
							}

						}
					}

				}

				@Override
				public void disconnected(SshDisconnectedEvent arg0) {

				}
			});
			ssh.setReadTimeout(0);
			ssh.setTimeout(0);
			ssh.connect();

			final OutputStream out = ssh.getOutputStream();

			return new SshClient() {
				@Override
				public void exec(String command) {
					if (command != null) {
						String line = command + "\n";
						try {
							out.write(line.getBytes());
							out.flush();
						} catch (Exception ioe) {
							ioe.printStackTrace();
						}
					}
				}

				@Override
				public void close() {
					ssh.disconnect();

				}

				@Override
				public String readLine() {
					try {
						return lines.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return null;
					}
				}

			};

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
