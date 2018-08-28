package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.Printer;

public abstract class GrepPrintFormater {

  protected String grep;
  protected Printer printer;

  protected int printLine(String str) {
    if (printer == null) {
      return 0;
    }
    if (str == null) {
      return 0;
    }
    if (grep != null) {
      if (str.indexOf(grep) == -1) {
        return 0;
      }
    }
    return printer.info(str);
  }

  protected void clear() {
    if (printer != null) {
      printer.clear();
    }
  }

  public void setPrinter(Printer printer) {
    this.printer = printer;
  }

}
