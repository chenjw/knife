package com.chenjw.knife.client.scanner;

import java.io.IOException;
import java.io.OutputStreamWriter;

import jline.ConsoleReader;

public class JlineScanner {
	private ConsoleReader reader;

	public JlineScanner() throws IOException {
		reader = new ConsoleReader(System.in,
				new OutputStreamWriter(System.out));
	}

	public String nextLine() throws IOException {
		return reader.readLine();
	}

	public void close() throws IOException {
		System.in.close();
	}
}
