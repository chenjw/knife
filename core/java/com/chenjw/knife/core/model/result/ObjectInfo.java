package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ObjectInfo implements Serializable {


  private static final long serialVersionUID = 4479892158579401619L;
  private String objectId;
  private String valueString;
  private long objectSize;
  private String hexHashCode;

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getValueString() {
    return valueString;
  }

  public void setValueString(String valueString) {
    this.valueString = valueString;
  }

  public long getObjectSize() {
    return objectSize;
  }

  public void setObjectSize(long objectSize) {
    this.objectSize = objectSize;
  }

  public String getHexHashCode() {
    return hexHashCode;
  }

  public void setHexHashCode(String hexHashCode) {
    this.hexHashCode = hexHashCode;
  }


}
