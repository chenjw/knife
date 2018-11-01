package com.chenjw.knife.client.formater;

import java.util.Map.Entry;
import com.chenjw.knife.core.model.result.Kv;
import com.chenjw.knife.core.model.result.SystemEnvInfo;

public class SystemEnvInfoFormater extends BasePrintFormater<SystemEnvInfo> {

  @Override
  protected void print(SystemEnvInfo info) {
    PreparedTableFormater table = new PreparedTableFormater(printer, grep);
    table.setTitle("type", "key", "value");
    for (Entry<String, String> entry : info.getSystemEnv().entrySet()) {
      table.addLine("System.env()",

          entry.getKey(),

          entry.getValue()

      );
    }

    for (Entry entry : info.getSystemProperties().entrySet()) {
      table.addLine("System.getProperties()",

          entry.getKey(),

          entry.getValue()

      );
    }

    for (Kv<String, String> entry : info.getVmInfo()) {
      table.addLine("vm-info",

          entry.getKey(),

          entry.getValue()

      );
    }

    table.print();
  }



}
