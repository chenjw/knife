package com.chenjw.knife.client.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setCompletors(String[]... strs) {

	}

}
