package com.chenjw.knife.agent.service;

import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.handler.perf.HeapProfilerNode;
import com.chenjw.knife.agent.handler.perf.HeapProfilerNodeKey;
import com.chenjw.knife.agent.handler.perf.HeapProfilerTree;


/**
 * 用来对指定类做新增对象统计
 * 
 * @author chenjw
 *
 */
public class HeapProfilerService implements Lifecycle {

  private volatile HeapProfilerTree heapProfilerTree;

  private static int MAX_STACK_DEPTH = 10;

  @Override
  public void init() {


  }

  public HeapProfilerTree startProfile() {
    heapProfilerTree = new HeapProfilerTree();
    return heapProfilerTree;
  }

  public void stopProfile() {
    heapProfilerTree = null;
  }

  public void onNewElement(String className, long objectSize) {
    if (heapProfilerTree == null) {
      return;
    }
    (new Throwable()).printStackTrace();
    StackTraceElement[] stes = (new Throwable()).getStackTrace();
    System.out.println("------start--------");
    for (StackTraceElement ste : stes) {
      System.out.println(ste.getClassName() + "." + ste.getMethodName());
    }
    System.out.println("------end--------");


    HeapProfilerNode currentNode = heapProfilerTree.getClassRoots().get(className);
    if (currentNode == null) {
      currentNode = new HeapProfilerNode();
      heapProfilerTree.getClassRoots().put(className, currentNode);
    }
    currentNode.getBytes().addAndGet(objectSize);
    int startIndex = InstrumentService.template.getStackTraceStartIndex(stes);
    System.out.println("startIndex=" + startIndex);
    System.out.println("className=" + className);
    int length = Math.min(stes.length, MAX_STACK_DEPTH + startIndex);
    for (int i = startIndex + 1; i < length; i++) {

      StackTraceElement ste = stes[i];

      HeapProfilerNodeKey key = new HeapProfilerNodeKey();
      key.setClassName(ste.getClassName());
      key.setMethodName(ste.getMethodName());
      HeapProfilerNode node = currentNode.getChildren().get(key);
      if (node == null) {
        node = new HeapProfilerNode();
        currentNode.getChildren().put(key, node);
      }
      node.getBytes().addAndGet(objectSize);
      currentNode = node;
    }

  }





  @Override
  public void clear() {

  }

  @Override
  public void close() {
    heapProfilerTree = null;

  }
}
