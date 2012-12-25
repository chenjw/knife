package com.chenjw.knife.utils;

/*
 * @(#)TelnetScriptExample.java
 *
 * Copyright (c) JSCAPE LLC.
 *
 * This software is the confidential and proprietary information of
 * JSCAPE. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with JSCAPE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.jscape.inet.telnet.DoOptionEvent;
import com.jscape.inet.telnet.Telnet;
import com.jscape.inet.telnet.TelnetAdapter;
import com.jscape.inet.telnet.TelnetConnectedEvent;
import com.jscape.inet.telnet.TelnetDataReceivedEvent;
import com.jscape.inet.telnet.TelnetDisconnectedEvent;
import com.jscape.inet.telnet.TelnetException;
import com.jscape.inet.telnet.TelnetOption;
import com.jscape.inet.telnet.TelnetScript;
import com.jscape.inet.telnet.TelnetTask;
import com.jscape.inet.telnet.WillOptionEvent;

public class TelnetScriptExample extends TelnetAdapter {

	private Telnet telnet = null;
	private TelnetScript script = null;
	private OutputStream output = null;
	private static BufferedReader reader = null;

	public TelnetScriptExample(String hostname) throws IOException,
			TelnetException {
		String loginPrompt = null;
		String login = null;
		String passwordPrompt = null;
		String password = null;
		String shellPrompt = null;
		String command = null;

		// create new Telnet instance
		telnet = new Telnet(hostname);

		// register for events
		telnet.addTelnetListener(this);

		// create new script
		script = new TelnetScript(telnet);

		// collect information to be used by script
		System.out.println("");
		System.out.println("Next you will enter your servers login prompt.");
		System.out
				.println("Note: this must match your servers login prompt exactly");
		System.out.print("Enter login prompt (e.g. login:): ");
		loginPrompt = reader.readLine();

		System.out.println("");
		System.out.print("Enter username (e.g. jsmith): ");
		login = reader.readLine();

		System.out.println("");
		System.out.println("Next you will enter your servers password prompt.");
		System.out
				.println("Note: this must match your servers password prompt exactly");
		System.out.print("Enter password prompt (e.g. Password:): ");
		passwordPrompt = reader.readLine();

		System.out.println("");
		System.out.print("Enter password (e.g. secret): ");
		password = reader.readLine();

		System.out.println("");
		System.out.println("Next you will enter your servers shell prompt.");
		System.out
				.println("Note: this must match your servers shell prompt exactly");
		System.out.print("Enter shell prompt - (e.g. $): ");
		shellPrompt = reader.readLine();

		System.out.println("");
		System.out
				.print("Enter command to run after successful login (e.g. ls -al): ");
		command = reader.readLine();

		// build tasks

		// build task to submit username
		TelnetTask loginTask = new TelnetTask(loginPrompt, login,
				passwordPrompt);

		// build task to submit password
		TelnetTask passwordTask = new TelnetTask(passwordPrompt, password,
				shellPrompt);

		// build task to execute command
		TelnetTask commandTask = new TelnetTask(shellPrompt, command,
				shellPrompt);

		// add all tasks to script
		script.addTask(loginTask);
		script.addTask(passwordTask);
		script.addTask(commandTask);

		// connect to Telnet server and execute script
		telnet.connect();

		// wait until last task is complete
		while (!commandTask.isComplete()) {
			try {

				Thread.sleep(1000);
			} catch (Exception e) {
			}

		}

		// disconnect from server
		telnet.disconnect();

	}

	public static void main(String[] args) {
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter Telnet server hostname (e.g. 10.0.0.1): ");
			String hostname = reader.readLine();
			TelnetScriptExample example = new TelnetScriptExample(hostname);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Invoked when Telnet socked is connected.
	 * 
	 * @see TelnetConnectedEvent
	 * @see Telnet#connect
	 */
	public void connected(TelnetConnectedEvent event) {
		System.out.println("Connected");
	}

	/**
	 * Invoked when Telnet socket is disconnected. Disconnect can occur in many
	 * circumstances including IOException during socket read/write.
	 * 
	 * @see TelnetDisconnectedEvent
	 * @see Telnet#disconnect
	 */
	public void disconnected(TelnetDisconnectedEvent event) {
		System.out.println("Disconnected");
	}

	/**
	 * Invoked when Telnet server requests that the Telnet client begin
	 * performing specified <code>TelnetOption</code>.
	 * 
	 * @param event
	 *            a <code>DoOptionEvent</code>
	 * @see DoOptionEvent
	 * @see TelnetOption
	 */
	public void doOption(DoOptionEvent event) {
		telnet.sendWontOption(event.getOption());
	}

	/**
	 * Invoked when Telnet server offers to begin performing specified
	 * <code>TelnetOption</code>.
	 * 
	 * @param event
	 *            a <code>WillOptionEvent</code>
	 * @see WillOptionEvent
	 * @see TelnetOption
	 */
	public void willOption(WillOptionEvent event) {
		telnet.sendDontOption(event.getOption());
	}

	/**
	 * Invoked when data is received from Telnet server.
	 * 
	 * @param event
	 *            a <code>TelnetDataReceivedEvent</code>
	 * @see TelnetDataReceivedEvent
	 */
	public void dataReceived(TelnetDataReceivedEvent event) {
		System.out.print(event.getData());
	}
}
