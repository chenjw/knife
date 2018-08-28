package com.chenjw.knife.client.model;

import java.util.Stack;

public class RetractBlock {
  private Stack<Integer> lines = new Stack<Integer>();

  public Stack<Integer> getLines() {
    return lines;
  }

  public void setLines(Stack<Integer> lines) {
    this.lines = lines;
  }


}
