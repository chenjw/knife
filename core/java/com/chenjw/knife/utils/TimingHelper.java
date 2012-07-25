package com.chenjw.knife.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.core.Printer;

public class TimingHelper {
	private static final String DEFAULT_NAME = "DEFAULT";
	private static final Printer DEFAULT_PRINTER = new Printer() {
		@Override
		public void println(String str) {
			System.out.println(str);
		}
	};
	private static Map<String, Long> RECORDS = new HashMap<String, Long>();
	private static final List<TimeSegment> STOP_SEGMENT_RECORDS = new ArrayList<TimeSegment>();
	private static volatile TimeSegment currentStopSegment = null;

	public static boolean isStoping() {
		return currentStopSegment == null;
	}

	/**
	 * stop timing
	 */
	public static void stop() {
		if (currentStopSegment == null) {
			currentStopSegment = new TimeSegment();
			currentStopSegment.setStart(System.nanoTime());
		}
	}

	/**
	 * resume timing
	 */
	public static void resume() {
		if (currentStopSegment != null) {
			currentStopSegment.setEnd(System.nanoTime());
			STOP_SEGMENT_RECORDS.add(currentStopSegment);
			currentStopSegment = null;
		}
	}

	/**
	 * start timing
	 */
	public static void start() {
		start(DEFAULT_NAME);
	}

	/**
	 * 
	 * start timing
	 * 
	 * @param name
	 */
	public static void start(String name) {
		RECORDS.put(name, System.nanoTime());
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
		Long start = RECORDS.get(name);
		if (start == null) {
			throw new RuntimeException("must start first!");
		}
		long end = System.nanoTime();
		// count stop time
		long stopTime = 0;
		for (TimeSegment stopTimeSegment : STOP_SEGMENT_RECORDS) {
			stopTime += stopTimeSegment.countSubTime(start, end);
		}
		if (currentStopSegment != null) {
			TimeSegment stopTimeSegment = new TimeSegment();
			stopTimeSegment.setStart(currentStopSegment.getStart());
			stopTimeSegment.setEnd(end);
			stopTime += stopTimeSegment.countSubTime(start, end);
		}
		// except stop time
		return end - start - stopTime;
	}

	public static void printMillis() {
		printMillis(DEFAULT_PRINTER, DEFAULT_NAME);
	}

	public static void printMillis(Printer printer) {
		printMillis(printer, DEFAULT_NAME);
	}

	public static void printMillis(String name) {
		printMillis(DEFAULT_PRINTER, name);
	}

	public static void printMillis(Printer printer, String name) {
		printer.println(name + " use " + getMillisInterval(name) + " ms");
	}

	public static void printNanos() {
		printNanos(DEFAULT_PRINTER, DEFAULT_NAME);
	}

	public static void printNanos(String name) {
		printNanos(DEFAULT_PRINTER, name);
	}

	public static void printNanos(Printer printer, String name) {
		printer.println(name + " use " + getNanosInterval(name) + " ns");
	}

	private static class TimeSegment {
		private long start;
		private long end;

		public long countSubTime(long s, long e) {
			long cs = s > start ? s : start;
			long ce = e < end ? e : end;
			long i = ce - cs;
			if (i < 0) {
				i = 0;
			}
			return i;
		}

		public long getStart() {
			return start;
		}

		public void setStart(long start) {
			this.start = start;
		}

		public long getEnd() {
			return end;
		}

		public void setEnd(long end) {
			this.end = end;
		}

	}

	public static void main(String[] args) throws InterruptedException {
		long i = System.currentTimeMillis();
		TimingHelper.start();
		Thread.sleep(1000);
		System.out.println(TimingHelper.getMillisInterval());
		System.out.println(System.currentTimeMillis() - i);
	}
}
