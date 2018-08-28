package com.chenjw.knife.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StringHelper {

  /**
   * 通配符匹配，仅支持*和?
   * 
   * @param pattern
   * @param str
   * @return
   */
  public static boolean matchIgnoreCase(String pattern, String str) {
    pattern = pattern.toLowerCase();
    str = str.toLowerCase();
    pattern = toJavaPattern(pattern);
    return Pattern.matches(pattern, str);
  }

  private static String toJavaPattern(String pattern) {
    String result = "^.*";
    char metachar[] = {'$', '^', '[', ']', '(', ')', '{', '|', '+', '.', '/'};
    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      boolean isMeta = false;
      for (int j = 0; j < metachar.length; j++) {
        if (ch == metachar[j]) {
          isMeta = true;
          break;
        }
      }
      if (!isMeta) {
        if (ch == '*' || ch == '?') {
          result += "." + ch;
        } else {
          result += ch;
        }
      } else {
        result += "\\" + ch;
      }
    }
    result += ".*$";
    return result;
  }

  public static String defaultString(String str) {
    return str != null ? str : "";
  }

  public static String defaultString(String str, String defaultStr) {
    return str != null ? str : defaultStr;
  }

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

  public static String join(Object array[], String separator, int startIndex, int endIndex) {
    if (array == null)
      return null;
    if (separator == null)
      separator = "";
    int bufSize = endIndex - startIndex;
    if (bufSize <= 0)
      return "";
    bufSize *= (array[startIndex] != null ? array[startIndex].toString().length() : 16)
        + separator.length();
    StringBuffer buf = new StringBuffer(bufSize);
    for (int i = startIndex; i < endIndex; i++) {
      if (i > startIndex)
        buf.append(separator);
      if (array[i] != null)
        buf.append(array[i]);
    }

    return buf.toString();
  }

  public static String join(Iterator<?> iterator, char separator) {
    if (iterator == null)
      return null;
    if (!iterator.hasNext())
      return "";
    Object first = iterator.next();
    if (!iterator.hasNext())
      if (first == null) {
        return null;
      } else {
        return first.toString();
      }
    StringBuffer buf = new StringBuffer(256);
    if (first != null)
      buf.append(first);
    do {
      if (!iterator.hasNext())
        break;
      buf.append(separator);
      Object obj = iterator.next();
      if (obj != null)
        buf.append(obj);
    } while (true);
    return buf.toString();
  }

  public static String join(Iterator<?> iterator, String separator) {
    if (iterator == null)
      return null;
    if (!iterator.hasNext())
      return "";
    Object first = iterator.next();
    if (!iterator.hasNext())
      if (first == null) {
        return null;
      } else {
        return first.toString();
      }
    StringBuffer buf = new StringBuffer(256);
    if (first != null)
      buf.append(first);
    do {
      if (!iterator.hasNext())
        break;
      if (separator != null)
        buf.append(separator);
      Object obj = iterator.next();
      if (obj != null)
        buf.append(obj);
    } while (true);
    return buf.toString();
  }

  public static String join(Collection<?> collection, char separator) {
    if (collection == null)
      return null;
    else
      return join(collection.iterator(), separator);
  }

  public static String join(Collection<?> collection, String separator) {
    if (collection == null)
      return null;
    else
      return join(collection.iterator(), separator);
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

  public static String replaceChars(String str, String searchChars, String replaceChars) {
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

  private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
    if (str == null)
      return null;
    int len = str.length();
    if (len == 0)
      return new String[0];
    List<String> list = new ArrayList<String>();
    int i = 0;
    int start = 0;
    boolean match = false;
    boolean lastMatch = false;
    while (i < len)
      if (str.charAt(i) == separatorChar) {
        if (match || preserveAllTokens) {
          list.add(str.substring(start, i));
          match = false;
          lastMatch = true;
        }
        start = ++i;
      } else {
        lastMatch = false;
        match = true;
        i++;
      }
    if (match || preserveAllTokens && lastMatch)
      list.add(str.substring(start, i));
    return (String[]) list.toArray(new String[list.size()]);
  }

  private static String[] splitWorker(String str, String separatorChars, int max,
      boolean preserveAllTokens) {
    if (str == null)
      return null;
    int len = str.length();
    if (len == 0)
      return new String[0];
    List<String> list = new ArrayList<String>();
    int sizePlus1 = 1;
    int i = 0;
    int start = 0;
    boolean match = false;
    boolean lastMatch = false;
    if (separatorChars == null)
      while (i < len)
        if (Character.isWhitespace(str.charAt(i))) {
          if (match || preserveAllTokens) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
        } else {
          lastMatch = false;
          match = true;
          i++;
        }
    else if (separatorChars.length() == 1) {
      char sep = separatorChars.charAt(0);
      while (i < len)
        if (str.charAt(i) == sep) {
          if (match || preserveAllTokens) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
        } else {
          lastMatch = false;
          match = true;
          i++;
        }
    } else {
      while (i < len)
        if (separatorChars.indexOf(str.charAt(i)) >= 0) {
          if (match || preserveAllTokens) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
        } else {
          lastMatch = false;
          match = true;
          i++;
        }
    }
    if (match || preserveAllTokens && lastMatch)
      list.add(str.substring(start, i));
    return (String[]) list.toArray(new String[list.size()]);
  }

  public static String[] split(String str) {
    return split(str, null, -1);
  }

  public static String[] split(String str, String separatorChars, int max) {
    return splitWorker(str, separatorChars, max, false);
  }

  public static String[] split(String str, char separatorChar) {
    return splitWorker(str, separatorChar, false);
  }

  public static String[] split(String str, String separatorChars) {
    return splitWorker(str, separatorChars, -1, false);
  }

  public static String repeat(String str, int repeat) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < repeat; i++) {
      sb.append(str);
    }
    return sb.toString();
  }
}
