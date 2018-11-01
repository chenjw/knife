package com.chenjw.knife.agent.event;

public class NewObjectEvent extends Event {
  private Object thisObject;
  private String className;
  private String constructorName;
  private Object[] arguments;


  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getConstructorName() {
    return constructorName;
  }

  public void setConstructorName(String constructorName) {
    this.constructorName = constructorName;
  }

  public Object[] getArguments() {
    return arguments;
  }

  public void setArguments(Object[] arguments) {
    this.arguments = arguments;
  }

  public Object getThisObject() {
    return thisObject;
  }

  public void setThisObject(Object thisObject) {
    this.thisObject = thisObject;
  }

}
