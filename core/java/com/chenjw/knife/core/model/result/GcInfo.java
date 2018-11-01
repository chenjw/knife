package com.chenjw.knife.core.model.result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GcInfo implements Serializable {
  /**
  * 
  */
  private static final long serialVersionUID = -2847788673767760859L;
  // 非堆内存使用
  private LongValue memNonHeapUsed = new LongValue();

  // 非堆内存申请
  private long memNonHeapCommitted = 0;
  // 堆内存使用
  private LongValue memHeapUsed = new LongValue();

  // 堆内存申请
  private long memHeapCommitted = 0;

  // younggc数量
  private LongValue ygc = new LongValue();
  // fullgc数量
  private LongValue fgc = new LongValue();



  private Map<String, Long> gcCounts = new HashMap<String, Long>();
  private Map<String, Long> gcTimes = new HashMap<String, Long>();

  public LongValue getMemNonHeapUsed() {
    return memNonHeapUsed;
  }

  public void setMemNonHeapUsed(LongValue memNonHeapUsed) {
    this.memNonHeapUsed = memNonHeapUsed;
  }

  public long getMemNonHeapCommitted() {
    return memNonHeapCommitted;
  }

  public void setMemNonHeapCommitted(long memNonHeapCommitted) {
    this.memNonHeapCommitted = memNonHeapCommitted;
  }

  public LongValue getMemHeapUsed() {
    return memHeapUsed;
  }

  public void setMemHeapUsed(LongValue memHeapUsed) {
    this.memHeapUsed = memHeapUsed;
  }

  public long getMemHeapCommitted() {
    return memHeapCommitted;
  }

  public void setMemHeapCommitted(long memHeapCommitted) {
    this.memHeapCommitted = memHeapCommitted;
  }

  public LongValue getYgc() {
    return ygc;
  }

  public void setYgc(LongValue ygc) {
    this.ygc = ygc;
  }

  public LongValue getFgc() {
    return fgc;
  }

  public void setFgc(LongValue fgc) {
    this.fgc = fgc;
  }

  public Map<String, Long> getGcCounts() {
    return gcCounts;
  }

  public void setGcCounts(Map<String, Long> gcCounts) {
    this.gcCounts = gcCounts;
  }

  public Map<String, Long> getGcTimes() {
    return gcTimes;
  }

  public void setGcTimes(Map<String, Long> gcTimes) {
    this.gcTimes = gcTimes;
  }



}
