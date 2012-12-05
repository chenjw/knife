package com.chenjw.knife.client.scanner;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;

public class JlineConsole {
	private ConsoleReader reader;

	public JlineConsole() throws IOException {
		reader = new ConsoleReader(System.in,
				new OutputStreamWriter(System.out));

	}

	@SuppressWarnings("rawtypes")
	public void setCompletors(String[]... strs) {
		// 清除旧的
		Collection cc = reader.getCompletors();
		if (cc != null) {
			for (Object o : cc) {
				Completor c = (Completor) o;
				reader.removeCompletor(c);
			}
		}
		// 生成新的
		List<Completor> completors = new ArrayList<Completor>();
		if (strs != null) {
			for (String[] comp : strs) {
				completors.add(new SimpleCompletor(comp));
			}
		}
		reader.addCompletor(new ArgumentCompletor(completors));
	}

	public String nextLine() throws IOException {
		return reader.readLine();
	}

	public void close() throws IOException {
		System.in.close();
	}
}
