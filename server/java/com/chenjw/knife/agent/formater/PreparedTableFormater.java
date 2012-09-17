package com.chenjw.knife.agent.formater;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.core.Printer;

public class PreparedTableFormater {
	private static final int BORDER = 2;
	private int[] width;
	private String[] title;
	private List<String[]> lines = new ArrayList<String[]>();

	public void setTitle(String... title) {
		this.title = title;
	}

	public void addLine(String... line) {
		lines.add(line);
	}

	private void doPrepare() {
		if (lines.size() == 0) {
			throw new RuntimeException("no line found");
		}
		int size = 0;
		if (title != null) {
			size = title.length;
		}
		for (String[] line : lines) {
			if (size == 0) {
				size = line.length;
			} else {
				if (size != line.length) {
					throw new RuntimeException("line width must be same");
				}
			}
		}
		if (width == null) {
			width = new int[size];
		}
		if (width.length != size) {
			throw new RuntimeException(
					"width length and line width must be same");
		}
		for (int i = 0; i < width.length; i++) {
			if (title != null) {
				if (title[i] != null) {
					int len = title[i].length();
					if (len > width[i]) {
						width[i] = len;
					}
				} else {
					title[i] = "";
				}
			}

			for (String[] line : lines) {
				if (line[i] != null) {
					int len = line[i].length();
					if (len > width[i]) {
						width[i] = len;
					}
				} else {
					line[i] = "";
				}
			}

		}

	}

	private void appendBlank(StringBuffer sb, int d, String ds) {
		for (int i = 0; i < d; i++) {
			sb.append(ds);
		}
	}

	public void print(Printer printer) {
		doPrepare();
		doPrint(printer);
	}

	private void printSeparatLine(Printer printer) {
		StringBuffer ss = new StringBuffer();
		for (int w : width) {
			appendBlank(ss, w + BORDER, "-");
		}
		printer.info(ss.toString());
	}

	private void doPrint(Printer printer) {
		if (title != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < title.length; i++) {
				int d = width[i] - title[i].length();
				sb.append(title[i]);
				appendBlank(sb, d + BORDER, " ");
			}
			printer.info(sb.toString());
		}
		printSeparatLine(printer);
		for (String[] line : lines) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < line.length; i++) {
				int d = width[i] - line[i].length();
				sb.append(line[i]);
				appendBlank(sb, d + BORDER, " ");
			}
			printer.info(sb.toString());
		}
		printSeparatLine(printer);
	}
}
