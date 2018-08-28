package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class HeapClassInfo implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -8249279063894335624L;
  private String name;
  private long instancesCount;
  private long instancesCountIncrement;
  private long bytes;
  private long bytesIncrement;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getInstancesCount() {
    return instancesCount;
  }

  public void setInstancesCount(long instancesCount) {
    this.instancesCount = instancesCount;
  }

  public long getBytes() {
    return bytes;
  }

  public void setBytes(long bytes) {
    this.bytes = bytes;
  }

  public long getInstancesCountIncrement() {
    return instancesCountIncrement;
  }

  public void setInstancesCountIncrement(long instancesCountIncrement) {
    this.instancesCountIncrement = instancesCountIncrement;
  }

  public long getBytesIncrement() {
    return bytesIncrement;
  }

  public void setBytesIncrement(long bytesIncrement) {
    this.bytesIncrement = bytesIncrement;
  }



}
