package com.chenjw.knife.client.formater;

import java.util.ArrayList;
import java.util.List;
import com.chenjw.knife.core.Printer;

public class PreparedTableFormater extends GrepPrintFormater {

  private static final int BORDER = 2;
  private int[] width;
  private String[] title;
  private List<Object[]> lines = new ArrayList<Object[]>();
  private int maxFieldWidth = 80;

  public PreparedTableFormater(Printer printer, String grep) {

    this.printer = printer;
    this.grep = grep;
  }

  protected void setTitle(String... title) {
    this.title = title;
  }

  protected void addLine(Object... line) {
    lines.add(line);
  }

  private void doPrepare() {
    int size = 0;
    if (title != null) {
      size = title.length;
    }
    for (Object[] line : lines) {
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
      throw new RuntimeException("width length and line width must be same");
    }
    for (int i = 0; i < width.length; i++) {
      if (title != null) {
        if (title[i] != null) {
          int len = title[i].length();
          if (len > maxFieldWidth) {
            len = maxFieldWidth;
          }
          if (len > width[i]) {
            width[i] = len;
          }
        } else {
          title[i] = "";
        }
      }

      for (Object[] line : lines) {
        if (line[i] != null) {
          int len = String.valueOf(line[i]).length();
          if (len > maxFieldWidth) {
            len = maxFieldWidth;
          }
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

  public final void print() {
    doPrepare();
    doPrint();
  }

  private void printSeparatLine() {
    StringBuffer ss = new StringBuffer();
    for (int w : width) {
      appendBlank(ss, w + BORDER, "-");
    }
    this.printLine(ss.toString());
  }

  private void doPrint() {
    if (title != null) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < title.length; i++) {
        int d = width[i] - title[i].length();
        sb.append(title[i]);
        appendBlank(sb, d + BORDER, " ");
      }
      this.printLine(sb.toString());
    }
    printSeparatLine();
    if (!lines.isEmpty()) {
      for (Object[] line : lines) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < line.length; i++) {
          int d = width[i] - String.valueOf(line[i]).length();
          sb.append(line[i]);
          appendBlank(sb, d + BORDER, " ");
        }
        this.printLine(sb.toString());
      }
    } else {

      this.printLine("not found!");
    }
    printSeparatLine();
  }

}
