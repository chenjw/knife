package com.chenjw.knife.client.formater;

public class StringFormater extends BasePrintFormater<String> {

  @Override
  protected void print(String str) {
    this.printLine(str);
  }

}
