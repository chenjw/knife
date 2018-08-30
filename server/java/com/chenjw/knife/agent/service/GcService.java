package com.chenjw.knife.agent.service;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.core.model.result.GcInfo;

public class GcService implements Lifecycle {

  public GcInfo collectGcInfo() {
    GcInfo gcInfo = new GcInfo();
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage memNonHeap = memoryMXBean.getNonHeapMemoryUsage();
    MemoryUsage memHeap = memoryMXBean.getHeapMemoryUsage();
    // 非堆内存
    gcInfo.setMemNonHeapUsed(memNonHeap.getUsed());
    gcInfo.setMemNonHeapCommitted(memNonHeap.getCommitted());
    // 堆内存
    gcInfo.setMemHeapUsed(memHeap.getUsed());
    gcInfo.setMemHeapCommitted(memHeap.getCommitted());
    int ygc = 0;
    int fgc = 0;
    //
    for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
      String name = bean.getName();
      long gcCount = bean.getCollectionCount();
      long gcTime = bean.getCollectionTime();
      gcInfo.getGcCounts().put(name, gcCount);
      gcInfo.getGcTimes().put(name, gcTime);
      if (isYgc(name)) {
        ygc += gcCount;
      } else if (isFgc(name)) {
        fgc += gcCount;
      }
    }
    gcInfo.setYgc(ygc);
    gcInfo.setFgc(fgc);
    return gcInfo;
  }

  private boolean isYgc(String name) {
    return "PS Scavenge".equals(name);
  }

  private boolean isFgc(String name) {
    return "PS MarkSweep".equals(name);
  }

  @Override
  public void init() {

  }

  @Override
  public void clear() {

  }

  @Override
  public void close() {

  }
}
