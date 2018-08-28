package com.chenjw.knife.client.formater;

import java.util.List;
import com.chenjw.knife.core.model.result.HeapClassInfo;
import com.chenjw.knife.core.model.result.HeapHistogram;

public class HeapHistogramFormater extends BasePrintFormater<HeapHistogram> {

  @Override
  protected void print(HeapHistogram heapHistogram) {

    PreparedTableFormater table = new PreparedTableFormater(printer, grep);
    table.setTitle("idx", "classname", "instances", "bytes", "bytes-percent");
    List<HeapClassInfo> classInfos = heapHistogram.getClasses();

    int i = 0;
    int instancesCount = 0;
    int bytesCount = 0;
    if (classInfos != null) {
      for (HeapClassInfo info : classInfos) {
        instancesCount += info.getInstancesCount();
        bytesCount += info.getBytes();
        table.addLine(i, info.getName(),
            numStr(info.getInstancesCount(), false, false)
                + wrap(numStr(info.getInstancesCountIncrement(), false, true)),
            numStr(info.getBytes(), true, false)
                + wrap(numStr(info.getBytesIncrement(), true, true)),
            String.format("%.2f", info.getBytes() * 100f / heapHistogram.getTotalBytes()) + "%");
        i++;
      }
      table.addLine("", "Other",
          numStr(heapHistogram.getTotalInstances() - instancesCount, false, false),
          numStr(heapHistogram.getTotalBytes() - bytesCount, true, false),
          String.format("%.2f",
              (heapHistogram.getTotalBytes() - bytesCount) * 100f / heapHistogram.getTotalBytes())
              + "%");
      table.addLine("", "Total",
          numStr(heapHistogram.getTotalInstances(), false, false)
              + wrap(numStr(heapHistogram.getTotalInstancesIncrement(), false, true)),
          numStr(heapHistogram.getTotalBytes(), true, false)
              + wrap(numStr(heapHistogram.getTotalBytesIncrement(), true, true)),
          String.format("%.2f",
              heapHistogram.getTotalBytes() * 100f / heapHistogram.getTotalBytes()) + "%");
    }
    table.print();
  }

  private String wrap(String str) {
    StringBuffer sb = new StringBuffer();
    sb.append(" (");
    sb.append(str);
    sb.append(")");
    return sb.toString();
  }

  private String numStr(long num, boolean isBytes, boolean isIncrement) {
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
