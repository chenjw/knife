package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class LongValue implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2603925024916648589L;
  private long increment = 0;
  private long value = 0;
  private long min = Integer.MAX_VALUE;
  private long max = Integer.MIN_VALUE;

  
  public LongValue() {
    
  }
  
  public LongValue(long value) {
    this.setValue(value);
  }
  
  public long getIncrement() {
    return increment;
  }

  public void setIncrement(long increment) {
    this.increment = increment;
  }

  public long getMin() {
    return min;
  }

  public void setMin(long min) {
    this.min = min;
  }

  public long getMax() {
    return max;
  }

  public void setMax(long max) {
    this.max = max;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
    this.min=Math.min(value, this.min);
    this.max=Math.max(value, this.max);
  }


}
