package com.chenjw.knife.agent.event;

public class NewArrayEvent extends Event {
  private Object thisObject;
  private String className;


  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Object getThisObject() {
    return thisObject;
  }

  public void setThisObject(Object thisObject) {
    this.thisObject = thisObject;
  }



}
