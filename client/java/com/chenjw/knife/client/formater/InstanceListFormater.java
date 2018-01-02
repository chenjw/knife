package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.InstanceListInfo;
import com.chenjw.knife.core.model.result.ObjectInfo;

public class InstanceListFormater extends BasePrintFormater<InstanceListInfo> {

  @Override
  protected void print(InstanceListInfo instanceListInfo) {
    PreparedTableFormater table = new PreparedTableFormater(printer, grep);
    table.setTitle("type", "obj-id", "detail", "hex-hash-code", "obj-size");
    ObjectInfo[] instanceInfos = instanceListInfo.getInstances();
    int i = 0;
    if (instanceInfos != null) {
      for (ObjectInfo info : instanceInfos) {
        table.addLine("[instance]", info.getObjectId(), info.getValueString(),
            info.getHexHashCode(), String.valueOf(info.getObjectSize()));
        i++;
      }
    }
    table.print();
    this.printLine("find " + i + " instances of " + instanceListInfo.getClassName());
  }

}
