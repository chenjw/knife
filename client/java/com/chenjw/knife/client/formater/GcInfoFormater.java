package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.GcInfo;

public class GcInfoFormater extends BasePrintFormater<GcInfo> {

  @Override
  protected void print(GcInfo gcInfo) {

    PreparedTableFormater table = new PreparedTableFormater(printer, grep);
    table.setTitle("idx", "key", "value", "percent", "increment");
    table.addLine(0, "ygc_count", gcInfo.getYgc(), "", gcInfo.getYgcIncrement());
    table.addLine(1, "fullgc_count", gcInfo.getFgc(), "", gcInfo.getFgcIncrement());

    table.addLine(2, "heap_used",
        numStr(gcInfo.getMemHeapUsed(), true, false) + " / "
            + numStr(gcInfo.getMemHeapCommitted(), true, false),
        String.format("%.2f", gcInfo.getMemHeapUsed() * 100f / gcInfo.getMemHeapCommitted()) + "%",
        numStr(gcInfo.getMemHeapUsedIncrement(), true, true));
    table.addLine(3, "non_heap_used",
        numStr(gcInfo.getMemNonHeapUsed(), true, false) + " / "
            + numStr(gcInfo.getMemNonHeapCommitted(), true, false),
        String.format("%.2f", gcInfo.getMemNonHeapUsed() * 100f / gcInfo.getMemNonHeapCommitted())
            + "%",
        numStr(gcInfo.getMemNonHeapUsedIncrement(), true, true));
    table.print();
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
