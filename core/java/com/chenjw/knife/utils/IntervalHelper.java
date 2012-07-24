package com.chenjw.knife.utils;

import java.util.HashMap;
import java.util.Map;

import com.chenjw.knife.core.Printer;

public class IntervalHelper {
	private static Map<String, Long> record = new HashMap<String, Long>();
	private static final String DEFAULT_NAME = "DEFAULT";
	private static final Printer DEFAULT_PRINTER = new Printer() {
		@Override
		public void println(String str) {
			System.out.println(str);
		}
	};

	public static void start() {
		start(DEFAULT_NAME);
	}

	public static void start(String name) {
		record.put(name, System.nanoTime());
	}

	public static long getMillisInterval() {
		return getMillisInterval(DEFAULT_NAME);
	}

	public static long getMillisInterval(String name) {
		return getNanosInterval(name) / 1000000;
	}

	public static long getNanosInterval() {
		return getNanosInterval(DEFAULT_NAME);
	}

	public static long getNanosInterval(String name) {
		Long t = record.get(name);
		if (t == null) {
			throw new RuntimeException("must start first!");
		}
		return System.nanoTime() - t;
	}

	public static void printMillis() {
		printMillis(DEFAULT_PRINTER, DEFAULT_NAME);
	}

	public static void printMillis(Printer printer) {
		printMillis(printer, DEFAULT_NAME);
	}

	public static void printMillis(Printer printer, String name) {
		// printer.println(name + " use " + getMillisInterval(name) + " ms");
	}

	public static void printNanos() {
		printNanos(DEFAULT_PRINTER, DEFAULT_NAME);
	}

	public static void printNanos(String name) {
		printNanos(DEFAULT_PRINTER, name);
	}

	public static void printNanos(Printer printer, String name) {
		// printer.println(name + " use " + getNanosInterval(name) + " ns");
	}

	public static void main(String[] args) throws InterruptedException {
		long i = System.currentTimeMillis();
		IntervalHelper.start();
		Thread.sleep(1000);
		System.out.println(IntervalHelper.getMillisInterval());
		System.out.println(System.currentTimeMillis() - i);
	}
}
