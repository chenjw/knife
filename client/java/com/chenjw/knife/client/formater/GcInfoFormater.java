package com.chenjw.knife.client.formater;

import com.chenjw.knife.client.utils.FormatUtils;
import com.chenjw.knife.core.model.result.GcInfo;

public class GcInfoFormater extends BasePrintFormater<GcInfo> {

  @Override
  protected void print(GcInfo gcInfo) {

    PreparedTableFormater table = new PreparedTableFormater(printer, grep);
    table.setTitle("idx", "key", "value");
    table.addLine(0,

        "ygc_count",

        FormatUtils.printLongValue(gcInfo.getYgc())

    );

    table.addLine(1,

        "fullgc_count",

        FormatUtils.printLongValue(gcInfo.getFgc())

    );

    table.addLine(2,

        "heap_used",

        FormatUtils.printLongValue(gcInfo.getMemHeapUsed()) + " / "
            + FormatUtils.printBytes(gcInfo.getMemHeapCommitted()) + " " + FormatUtils
                .printPercent(gcInfo.getMemHeapUsed().getValue(), gcInfo.getMemHeapCommitted())

    );
    table.addLine(3,

        "non_heap_used",

        FormatUtils.printLongValue(gcInfo.getMemNonHeapUsed()) + " / "
            + FormatUtils.printBytes(gcInfo.getMemNonHeapCommitted()) + " "
            + FormatUtils.printPercent(gcInfo.getMemNonHeapUsed().getValue(),
                gcInfo.getMemNonHeapCommitted())

    );
    table.print();
  }



}
