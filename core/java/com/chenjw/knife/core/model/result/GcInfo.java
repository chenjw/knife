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
  private long memNonHeapUsed;
  // 非堆内存使用增量
  private long memNonHeapUsedIncrement=0;
  // 非堆内存申请
  private long memNonHeapCommitted;
  // 堆内存使用
  private long memHeapUsed;
  // 堆内存使用增量
  private long memHeapUsedIncrement=0;
  // 堆内存申请
  private long memHeapCommitted;

  // younggc数量
  private long ygc;
  // fullgc数量
  private long fgc;
  // younggc增量
  private long ygcIncrement = 0;
  // fullgc增量
  private long fgcIncrement = 0;


  private Map<String, Long> gcCounts = new HashMap<String, Long>();
  private Map<String, Long> gcTimes = new HashMap<String, Long>();

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

  public long getMemNonHeapUsed() {
    return memNonHeapUsed;
  }

  public void setMemNonHeapUsed(long memNonHeapUsed) {
    this.memNonHeapUsed = memNonHeapUsed;
  }

  public long getMemNonHeapCommitted() {
    return memNonHeapCommitted;
  }

  public void setMemNonHeapCommitted(long memNonHeapCommitted) {
    this.memNonHeapCommitted = memNonHeapCommitted;
  }

  public long getMemHeapUsed() {
    return memHeapUsed;
  }

  public void setMemHeapUsed(long memHeapUsed) {
    this.memHeapUsed = memHeapUsed;
  }

  public long getMemHeapCommitted() {
    return memHeapCommitted;
  }

  public void setMemHeapCommitted(long memHeapCommitted) {
    this.memHeapCommitted = memHeapCommitted;
  }

  public long getYgc() {
    return ygc;
  }

  public void setYgc(long ygc) {
    this.ygc = ygc;
  }

  public long getFgc() {
    return fgc;
  }

  public void setFgc(long fgc) {
    this.fgc = fgc;
  }

  public long getYgcIncrement() {
    return ygcIncrement;
  }

  public void setYgcIncrement(long ygcIncrement) {
    this.ygcIncrement = ygcIncrement;
  }

  public long getFgcIncrement() {
    return fgcIncrement;
  }

  public void setFgcIncrement(long fgcIncrement) {
    this.fgcIncrement = fgcIncrement;
  }

  public long getMemNonHeapUsedIncrement() {
    return memNonHeapUsedIncrement;
  }

  public void setMemNonHeapUsedIncrement(long memNonHeapUsedIncrement) {
    this.memNonHeapUsedIncrement = memNonHeapUsedIncrement;
  }

  public long getMemHeapUsedIncrement() {
    return memHeapUsedIncrement;
  }

  public void setMemHeapUsedIncrement(long memHeapUsedIncrement) {
    this.memHeapUsedIncrement = memHeapUsedIncrement;
  }



}
