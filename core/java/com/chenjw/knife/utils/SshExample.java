package com.chenjw.knife.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.jscape.inet.ssh.Ssh;
import com.jscape.inet.ssh.SshConnectedEvent;
import com.jscape.inet.ssh.SshDataReceivedEvent;
import com.jscape.inet.ssh.SshDisconnectedEvent;
import com.jscape.inet.ssh.SshListener;
import com.jscape.inet.ssh.util.SshParameters;

public class SshExample implements SshListener {

	// state of SSH connection
	private boolean connected = false;

	/**
	 * Creates a new SshExample instance.
	 * 
	 */
	public SshExample() {
		String hostname = null;
		String username = null;
		String password = null;
		Ssh ssh = null;

		try {
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.print("Enter SSH hostname: ");
			hostname = bin.readLine();

			System.out.print("Enter SSH username: ");
			username = bin.readLine();

			System.out.print("Enter SSH password: ");
			password = bin.readLine();

			// create new Ssh instance
			SshParameters sshParams = new SshParameters(hostname, username,
					password);

			ssh = new Ssh(sshParams);

			// register to capture events
			ssh.addSshListener(this);

			System.out.println("Connecting please wait...");

			// connect
			ssh.connect();

			// get output stream for writing data to SSH server
			OutputStream out = ssh.getOutputStream();

			// holds line entered at console
			String line = null;

			// read data from console
			while (connected && (line = bin.readLine()) != null) {
				// send line with LF to SSH server
				line += "\n";
				try {
					out.write(line.getBytes());
					out.flush();
				} catch (Exception ioe) {
					connected = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connected) {
					ssh.disconnect();
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Captures SshConnectedEvent
	 */
	public void connected(SshConnectedEvent ev) {
		System.out.println("Connected: " + ev.getHost());
		connected = true;
	}

	/**
	 * Captures SshDataReceivedEvent
	 */
	public void dataReceived(SshDataReceivedEvent ev) {
		// send data received to console
		System.out.print(ev.getData());
	}

	/**
	 * Captures SshDisconnectedEvent
	 */
	public void disconnected(SshDisconnectedEvent ev) {
		System.out.println("Disconnected: " + ev.getHost()
				+ ". Press Enter to exit");
		connected = false;
	}

	/**
	 * Main method for SshExample
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SshExample test = new SshExample();
	}

}
