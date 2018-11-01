package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class HeapClassInfo implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -8249279063894335624L;
  private String name;
  private LongValue instancesCount=new LongValue();

  private LongValue bytes=new LongValue();


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LongValue getInstancesCount() {
    return instancesCount;
  }

  public void setInstancesCount(LongValue instancesCount) {
    this.instancesCount = instancesCount;
  }

  public LongValue getBytes() {
    return bytes;
  }

  public void setBytes(LongValue bytes) {
    this.bytes = bytes;
  }

}
