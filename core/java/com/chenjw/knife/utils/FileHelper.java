package com.chenjw.knife.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileHelper {
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory())
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			if (!file.canRead())
				throw new IOException("File '" + file + "' cannot be read");
			else
				return new FileInputStream(file);
		} else {
			throw new FileNotFoundException("File '" + file
					+ "' does not exist");
		}
	}

	public static String readFileToString(File file, String encoding)
			throws IOException {
		java.io.InputStream in = null;
		try {
			in = openInputStream(file);
			return IOHelper.toString(in, encoding);
		} finally {
			IOHelper.closeQuietly(in);
		}
	}

	public static void writeStringToFile(File file, String data, String encoding)
			throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			IOHelper.write(data, out, encoding);
		} finally {
			IOHelper.closeQuietly(out);
		}
	}

	public static void forceMkdir(File directory) throws IOException {
		if (directory.exists()) {
			if (directory.isFile()) {
				String message = "File " + directory + " exists and is "
						+ "not a directory. Unable to create directory.";
				throw new IOException(message);
			}
		} else if (!directory.mkdirs()) {
			String message = "Unable to create directory " + directory;
			throw new IOException(message);
		}
	}

	public static void writeByteArrayToFile(File file, byte[] data)
			throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			out.write(data);
		} finally {
			IOHelper.closeQuietly(out);
		}
	}

	public static FileOutputStream openOutputStream(File file)
			throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file
						+ "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file
							+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file);
	}
}
