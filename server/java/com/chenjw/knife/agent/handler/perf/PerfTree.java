package com.chenjw.knife.agent.handler.perf;

import java.util.HashMap;
import java.util.Map;

public class PerfTree {

  private Map<String, PerfNode> threadRoots = new HashMap<String, PerfNode>();

  public Map<String, PerfNode> getThreadRoots() {
    return threadRoots;
  }

  public void setThreadRoots(Map<String, PerfNode> threadRoots) {
    this.threadRoots = threadRoots;
  }



}
