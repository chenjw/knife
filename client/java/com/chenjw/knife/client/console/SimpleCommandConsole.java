package com.chenjw.knife.client.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleCommandConsole extends CommandConsoleTemplate {
	private static final String OUT_PREFIX = "knife>";
	private BufferedReader reader;

	public SimpleCommandConsole() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		start();
	}

	@Override
	public void close() throws Exception {
		reader.close();
	}

	@Override
	public String readConsoleLine() {
		String line = null;
		while (true) {
			try {
				line = reader.readLine();

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (line != null) {
				return line;
			}
		}
	}

	@Override
	public void writeConsoleLine(String line) {
		System.out.println(OUT_PREFIX + line);
	}

	public void setCompletors(String[]... strs) {

	}

}
