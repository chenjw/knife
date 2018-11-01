package com.chenjw.knife.client.formater;

import java.util.List;
import com.chenjw.knife.client.utils.FormatUtils;
import com.chenjw.knife.core.model.result.HeapClassInfo;
import com.chenjw.knife.core.model.result.HeapHistogram;

public class HeapHistogramFormater extends BasePrintFormater<HeapHistogram> {

  @Override
  protected void print(HeapHistogram heapHistogram) {

    PreparedTableFormater table = new PreparedTableFormater(printer, grep);
    table.setTitle("idx", "classname", "instances", "bytes", "bytes-percent","bytes-min-max");
    List<HeapClassInfo> classInfos = heapHistogram.getClasses();

    int i = 0;
    long instancesCount = 0;
    long bytesCount = 0;
    if (classInfos != null) {
      for (HeapClassInfo info : classInfos) {
        instancesCount += info.getInstancesCount().getValue();
        bytesCount += info.getBytes().getValue();
        table.addLine(i,

            info.getName(),

            info.getInstancesCount().getValue(),

            FormatUtils.printLongValue(info.getBytes()),

            FormatUtils.printPercent(info.getBytes().getValue(),
                heapHistogram.getTotalBytes().getValue()),
            
            FormatUtils.printMinMax(info.getBytes(), true)

        );
        i++;
      }
      table.addLine("",

          "Other",

          heapHistogram.getTotalInstances().getValue() - instancesCount,

          FormatUtils.printBytes(heapHistogram.getTotalBytes().getValue() - bytesCount),

          FormatUtils.printPercent(heapHistogram.getTotalBytes().getValue() - bytesCount,
              heapHistogram.getTotalBytes().getValue()),
          
          ""

      );
      table.addLine("",

          "Total",

          heapHistogram.getTotalInstances().getValue(),

          FormatUtils.printLongValue(heapHistogram.getTotalBytes()),

          FormatUtils.printPercent(heapHistogram.getTotalBytes().getValue(),
              heapHistogram.getTotalBytes().getValue()),
          
          FormatUtils.printMinMax(heapHistogram.getTotalBytes(), true)

      );
    }
    table.print();
  }


}
