package com.chenjw.knife.utils;

public class StringHelper {
	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		int sz = str.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isDigit(str.charAt(i)))
				return false;

		return true;
	}

	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null)
			return str;
		if (separator.length() == 0)
			return "";
		int pos = str.indexOf(separator);
		if (pos == -1)
			return str;
		else
			return str.substring(0, pos);
	}

	public static String substringBeforeLast(String str, String separator) {
		if (isEmpty(str) || isEmpty(separator))
			return str;
		int pos = str.lastIndexOf(separator);
		if (pos == -1)
			return str;
		else
			return str.substring(0, pos);
	}

	public static String substringAfterLast(String str, String separator) {
		if (isEmpty(str))
			return str;
		if (isEmpty(separator))
			return "";
		int pos = str.lastIndexOf(separator);
		if (pos == -1 || pos == str.length() - separator.length())
			return "";
		else
			return str.substring(pos + separator.length());
	}

	public static String substringAfter(String str, String separator) {
		if (isEmpty(str))
			return str;
		if (separator == null)
			return "";
		int pos = str.indexOf(separator);
		if (pos == -1)
			return "";
		else
			return str.substring(pos + separator.length());
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean equals(String str1, String str2) {
		return str1 != null ? str1.equals(str2) : str2 == null;
	}

	public static String join(Object array[], String separator) {
		if (array == null)
			return null;
		else
			return join(array, separator, 0, array.length);
	}

	public static String join(Object array[], String separator, int startIndex,
			int endIndex) {
		if (array == null)
			return null;
		if (separator == null)
			separator = "";
		int bufSize = endIndex - startIndex;
		if (bufSize <= 0)
			return "";
		bufSize *= (array[startIndex] != null ? array[startIndex].toString()
				.length() : 16) + separator.length();
		StringBuffer buf = new StringBuffer(bufSize);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex)
				buf.append(separator);
			if (array[i] != null)
				buf.append(array[i]);
		}

		return buf.toString();
	}

	public static int indexOf(String str, char searchChar, int startPos) {
		if (isEmpty(str))
			return -1;
		else
			return str.indexOf(searchChar, startPos);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0)
			return true;
		for (int i = 0; i < strLen; i++)
			if (!Character.isWhitespace(str.charAt(i)))
				return false;

		return true;
	}

	public static String replaceChars(String str, String searchChars,
			String replaceChars) {
		if (isEmpty(str) || isEmpty(searchChars))
			return str;
		if (replaceChars == null)
			replaceChars = "";
		boolean modified = false;
		int replaceCharsLength = replaceChars.length();
		int strLength = str.length();
		StringBuffer buf = new StringBuffer(strLength);
		for (int i = 0; i < strLength; i++) {
			char ch = str.charAt(i);
			int index = searchChars.indexOf(ch);
			if (index >= 0) {
				modified = true;
				if (index < replaceCharsLength)
					buf.append(replaceChars.charAt(index));
			} else {
				buf.append(ch);
			}
		}

		if (modified)
			return buf.toString();
		else
			return str;
	}

	public static String substring(String str, int start) {
		if (str == null)
			return null;
		if (start < 0)
			start = str.length() + start;
		if (start < 0)
			start = 0;
		if (start > str.length())
			return "";
		else
			return str.substring(start);
	}

	public static String substring(String str, int start, int end) {
		if (str == null)
			return null;
		if (end < 0)
			end = str.length() + end;
		if (start < 0)
			start = str.length() + start;
		if (end > str.length())
			end = str.length();
		if (start > end)
			return "";
		if (start < 0)
			start = 0;
		if (end < 0)
			end = 0;
		return str.substring(start, end);
	}
}
