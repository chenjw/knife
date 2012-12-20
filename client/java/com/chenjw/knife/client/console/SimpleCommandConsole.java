package com.chenjw.knife.client.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleCommandConsole extends CommandConsole {
	private static final String OUT_PREFIX = "knife>";
	private BufferedReader reader;

	public SimpleCommandConsole() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void close() throws Exception {
		reader.close();
	}

	@Override
	public String readConsoleLine() throws Exception {
		String line;
		while (true) {
			if ((line = reader.readLine()) != null) {
				return line;
			} else {
				Thread.sleep(1000);
			}
		}
	}

	@Override
	public void writeConsoleLine(String line) throws Exception {
		System.out.println(OUT_PREFIX + line);
	}

	public void setCompletors(String[]... strs) {

	}

}
