package com.chenjw.knife.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOHelper {

	public static int readBytes(InputStream is, byte[] bytes)
			throws IOException {
		int i = 0;
		while (i < bytes.length) {
			int a = is.read(bytes, i, bytes.length - i);
			if (a == -1) {
				break;
			} 

			else {
				i += a;
			}
		}
		return i;

	}

	public static long copyLarge(Reader input, Writer output)
			throws IOException {
		char buffer[] = new char[4096];
		long count = 0L;
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}

	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L)
			return -1;
		else
			return (int) count;
	}

	public static void copy(InputStream input, Writer output)
			throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(((Reader) (in)), output);
	}

	public static void copy(InputStream input, Writer output, String encoding)
			throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy(((Reader) (in)), output);
		}
	}

	public static String toString(InputStream input, String encoding)
			throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, encoding);
		return sw.toString();
	}

	public static void write(String data, OutputStream output)
			throws IOException {
		if (data != null)
			output.write(data.getBytes());
	}

	public static void write(String data, OutputStream output, String encoding)
			throws IOException {
		if (data != null)
			if (encoding == null)
				write(data, output);
			else
				output.write(data.getBytes(encoding));
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L)
			return -1;
		else
			return (int) count;
	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte buffer[] = new byte[4096];
		long count = 0L;
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}

	public static void closeQuietly(InputStream input) {
		try {
			if (input != null)
				input.close();
		} catch (IOException ioe) {
		}
	}

	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null)
				output.close();
		} catch (IOException ioe) {
		}
	}

}
