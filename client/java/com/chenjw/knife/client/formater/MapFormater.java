package com.chenjw.knife.client.formater;

import com.chenjw.knife.core.model.result.EntryInfo;
import com.chenjw.knife.core.model.result.MapInfo;

public class MapFormater extends BasePrintFormater<MapInfo> {

    protected void print(MapInfo arrayInfo) {
        PreparedTableFormater table = new PreparedTableFormater(printer, grep);
        table.setTitle("idx", "key-obj-id", "key-element", " = ", "value-obj-id", "value-element");
        EntryInfo[] entryInfos = arrayInfo.getElements();
        if (entryInfos != null) {
            int i = 0;
            for (EntryInfo element : entryInfos) {
                table.addLine(String.valueOf(i), element.getKey().getObjectId(), element.getKey()
                    .getValueString(), " = ", element.getValue().getObjectId(), element.getValue()
                    .getValueString());
                i++;

            }

        }
        table.print();
        printLine("finished!");
    }

}
