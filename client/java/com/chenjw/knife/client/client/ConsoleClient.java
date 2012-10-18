package com.chenjw.knife.client.client;

import java.io.IOException;

import com.chenjw.knife.client.scanner.JlineScanner;

public class ConsoleClient extends BaseClient {
	private static final String OUT_PREFIX = "knife>";
	private JlineScanner stdin;

	public ConsoleClient() {
		try {
			stdin = new JlineScanner();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		stdin.close();

	}

	@Override
	public String readLine() throws Exception {
		String line = stdin.nextLine();
		return line;
	}

	@Override
	public void writeLine(String line) throws Exception {
		System.out.println(OUT_PREFIX + line);
	}

}
