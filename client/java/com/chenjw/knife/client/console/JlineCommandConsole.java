package com.chenjw.knife.client.console;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;

public class JlineCommandConsole extends CommandConsoleTemplate {
	private static final String OUT_PREFIX = "knife>";
	private ConsoleReader reader;

	public JlineCommandConsole() {
		try {

			reader = new ConsoleReader(System.in, new OutputStreamWriter(
					System.out));

		} catch (IOException e) {
			e.printStackTrace();
		}
		this.start();
	}

	@Override
	public void close() throws Exception {
		System.in.close();

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setCompletors(String[]... strs) {
		// 清除旧的
		Collection cc = reader.getCompletors();
		if (cc != null) {
			for (Object o : cc.toArray(new Object[cc.size()])) {
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

}
