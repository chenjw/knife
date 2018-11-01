package com.chenjw.knife.agent.handler.perf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HeapProfilerNode {

  private Map<HeapProfilerNodeKey, HeapProfilerNode> children =
      new ConcurrentHashMap<HeapProfilerNodeKey, HeapProfilerNode>();

  private AtomicLong num = new AtomicLong(0);

  private AtomicLong bytes = new AtomicLong(0);

  public Map<HeapProfilerNodeKey, HeapProfilerNode> getChildren() {
    return children;
  }

  public void setChildren(Map<HeapProfilerNodeKey, HeapProfilerNode> children) {
    this.children = children;
  }

  public AtomicLong getNum() {
    return num;
  }

  public void setNum(AtomicLong num) {
    this.num = num;
  }

  public AtomicLong getBytes() {
    return bytes;
  }

  public void setBytes(AtomicLong bytes) {
    this.bytes = bytes;
  }

}
