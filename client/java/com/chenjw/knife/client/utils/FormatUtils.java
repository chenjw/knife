package com.chenjw.knife.client.utils;

import com.chenjw.knife.core.model.result.LongValue;

public class FormatUtils {


  public static String printPercent(long value, long total) {
    return String.format("%.2f", value * 100f / total) + "%";
  }

  public static String printLongValue(LongValue value) {
    return printLongValue(value, true);
  }

  public static String printLongValue(LongValue value, boolean isBytes) {
    return printLongValue(value, true, true, false);
  }

  /**
   * 
   * 123k (+22m) [22m-300k]
   * 
   * @param value
   * @param isBytes
   * @return
   */
  public static String printLongValue(LongValue value, boolean isBytes, boolean addIncrement,
      boolean addMinMax) {
    StringBuffer sb = new StringBuffer();
    sb.append(printNum(value.getValue(), isBytes, false));
    if (addIncrement) {
      sb.append(" (");
      sb.append(printNum(value.getIncrement(), isBytes, true));
      sb.append(")");
    }
    if (addMinMax) {
      sb.append(" [");
      sb.append(printMinMax(value, isBytes));
      sb.append("]");
    }
    return sb.toString();
  }

  public static String printMinMax(LongValue value, boolean isBytes) {
    StringBuffer sb = new StringBuffer();
    sb.append(printNum(value.getMin(), isBytes, false));
    sb.append("-");
    sb.append(printNum(value.getMax(), isBytes, false));
    return sb.toString();
  }

  public static String printIncrement(long num, boolean isBytes) {
    return printNum(num, isBytes, true);
  }

  public static String printBytes(long num) {
    return printNum(num, true, false);
  }

  private static String printNum(long num, boolean isBytes, boolean isIncrement) {
    StringBuffer sb = new StringBuffer();
    if (isIncrement) {
      if (num >= 0) {
        sb.append("+");
      } else {
        sb.append("-");
        num = -num;
      }
    }
    String numStr = String.valueOf(num);
    if (isBytes) {
      if (num > 1024 * 1024 * 1024) {
        numStr = String.format("%.1fg", num * 1f / (1024 * 1024 * 1024));
      } else if (num > 1024 * 1024) {
        numStr = String.format("%.1fm", num * 1f / (1024 * 1024));
      } else if (num > 1024) {
        numStr = String.format("%.1fk", num * 1f / (1024));
      }
    }
    sb.append(numStr);
    return sb.toString();
  }
}
