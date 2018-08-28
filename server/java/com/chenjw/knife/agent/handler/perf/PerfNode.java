package com.chenjw.knife.agent.handler.perf;

import java.util.HashMap;
import java.util.Map;

public class PerfNode {

  private Map<PerfNodeKey, PerfNode> children = new HashMap<PerfNodeKey, PerfNode>();

  private long timecost = 0;

  private long timecostCpu = 0;

  private long selfTimecost = 0;

  private long selfTimecostCpu = 0;

  public Map<PerfNodeKey, PerfNode> getChildren() {
    return children;
  }

  public void setChildren(Map<PerfNodeKey, PerfNode> children) {
    this.children = children;
  }

  public long getTimecost() {
    return timecost;
  }

  public void setTimecost(long timecost) {
    this.timecost = timecost;
  }

  public long getTimecostCpu() {
    return timecostCpu;
  }

  public void setTimecostCpu(long timecostCpu) {
    this.timecostCpu = timecostCpu;
  }

  public long getSelfTimecost() {
    return selfTimecost;
  }

  public void setSelfTimecost(long selfTimecost) {
    this.selfTimecost = selfTimecost;
  }

  public long getSelfTimecostCpu() {
    return selfTimecostCpu;
  }

  public void setSelfTimecostCpu(long selfTimecostCpu) {
    this.selfTimecostCpu = selfTimecostCpu;
  }



}
