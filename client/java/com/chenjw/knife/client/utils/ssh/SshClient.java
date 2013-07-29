package com.chenjw.knife.client.utils.ssh;

public interface SshClient {
	public void exec(String command);

	public String readLine();

	public void close();
}
