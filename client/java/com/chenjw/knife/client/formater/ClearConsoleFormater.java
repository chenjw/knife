package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.ClearConsoleInfo;

public class ClearConsoleFormater extends BasePrintFormater<ClearConsoleInfo> {

  @Override
  protected void print(ClearConsoleInfo retractInfo) {
    this.clear();
  }

}
