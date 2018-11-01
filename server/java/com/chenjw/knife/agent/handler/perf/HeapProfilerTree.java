package com.chenjw.knife.agent.handler.perf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeapProfilerTree {

  private Map<String, HeapProfilerNode> classRoots = new ConcurrentHashMap<String, HeapProfilerNode>();

  public Map<String, HeapProfilerNode> getClassRoots() {
    return classRoots;
  }

  public void setClassRoots(Map<String, HeapProfilerNode> classRoots) {
    this.classRoots = classRoots;
  }



}
