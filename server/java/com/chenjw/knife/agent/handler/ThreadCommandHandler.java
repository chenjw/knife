package com.chenjw.knife.agent.handler;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.handler.perf.PerfNode;
import com.chenjw.knife.agent.handler.perf.PerfNodeKey;
import com.chenjw.knife.agent.handler.perf.PerfTree;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.utils.StringHelper;



public class ThreadCommandHandler implements CommandHandler {

  private long perfInterval = 100;


  private void mergeThreadInfoToNode(PerfNode perfNode, ThreadInfo ts, StackTraceElement[] stes) {
    
    perfNode.setTimecost(perfNode.getTimecost() + perfInterval);
    if (ts.getThreadState() == Thread.State.RUNNABLE) {
      perfNode.setTimecostCpu(perfNode.getTimecostCpu() + perfInterval);
    }
    if (stes == null) {
      return;
    }
    PerfNode currentNode = perfNode;
    for (int i = stes.length - 1; i >= 0; i--) {
      StackTraceElement ste = stes[i];
      PerfNodeKey key = new PerfNodeKey();
      key.setClassName(ste.getClassName());
      key.setMethodName(ste.getMethodName());
      PerfNode node = currentNode.getChildren().get(key);
      if (node == null) {
        node = new PerfNode();
        currentNode.getChildren().put(key, node);
      }
      node.setTimecost(node.getTimecost() + perfInterval);
      if (ts.getThreadState() == Thread.State.RUNNABLE) {
        node.setTimecostCpu(node.getTimecostCpu() + perfInterval);
      }
      // 当前节点是最后节点，说明时间都是被这个节点占用的
      if (i == 0) {
        node.setSelfTimecost(node.getSelfTimecost() + perfInterval);
        if (ts.getThreadState() == Thread.State.RUNNABLE) {
          node.setSelfTimecostCpu(node.getSelfTimecostCpu() + perfInterval);
        }
      }
      currentNode = node;
    }
  }

  /**
   * 把线程采样数据累积到方法节点上
   */
  private void addThreadInfo(PerfNode perfNode, String className, String methodName,
      ThreadInfo[] tis) {
    for (ThreadInfo ts : tis) {
      StackTraceElement[] stes = ts.getStackTrace();
      if (stes == null) {
        continue;
      }
      for (int i = stes.length - 1; i >= 0; i--) {

        StackTraceElement ste = stes[i];
        // 如果匹配到就解析
        if (StringHelper.equals(className, ste.getClassName())
            && StringHelper.equals(methodName, ste.getMethodName())) {
          StackTraceElement[] subStes = Arrays.copyOfRange(stes, 0, i+1);
          this.mergeThreadInfoToNode(perfNode, ts, subStes);
          break;
        } else {
          continue;
        }


      }
    }
  }

  /**
   * 把线程采样数据累积到线程树上
   * 
   * @param perfTree
   * @param tis
   */
  private void addThreadInfo(PerfTree perfTree, ThreadInfo[] tis) {
    for (ThreadInfo ts : tis) {
      PerfNode perfNode = perfTree.getThreadRoots().get(ts.getThreadName());
      if (perfNode == null) {
        perfNode = new PerfNode();
        perfTree.getThreadRoots().put(ts.getThreadName(), perfNode);
      }
      StackTraceElement[] stes = ts.getStackTrace();
      this.mergeThreadInfoToNode(perfNode, ts, stes);
    }

  }

  public static void main(String[] args) throws InterruptedException {
    ThreadCommandHandler c=new ThreadCommandHandler();
    int second = 5;
    int t = (int) (second * 1000 / c.perfInterval);
    PerfTree tree = new PerfTree();
    for (int i = 0; i < t; i++) {
      ThreadInfo[] tis = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);

      // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(tis, true)));
      c.addThreadInfo(tree, tis);
      Thread.sleep(c.perfInterval);
    }
    // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(root, true)));
    c.printTree(tree);
    PerfNode node = new PerfNode();
    for (int i = 0; i < t; i++) {
      ThreadInfo[] tis = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);

      // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(tis, true)));
      c.addThreadInfo(node,"com.chenjw.knife.agent.handler.ThreadCommandHandler","main", tis);
      Thread.sleep(c.perfInterval);
    }
    // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(root, true)));
    c.printSubNode(node.getTimecost(), 0, node);
  }
  

  private void perfThread(Args args) throws InterruptedException {
    // 默认perf10秒
    int second = 5;
    Map<String, String> tOptions = args.option("-t");
    if (tOptions != null) {
      second = Integer.parseInt(tOptions.get("time"));
    }
    //
    Agent.sendPart(ResultHelper.newFragment("start sampling wait " + second + " seconds..."));
    int t = (int) (second * 1000 / perfInterval);
    Map<String, String> mOptions = args.option("-m");
    String className = null;
    String methodName = null;
    if (mOptions != null) {
      String method = mOptions.get("method");
      className = StringHelper.substringBeforeLast(method, ".");
      methodName = StringHelper.substringAfterLast(method, ".");
    }
    // 统计某个方法内部的耗时
    if (className!=null) {
      PerfNode node = new PerfNode();
      for (int i = 0; i < t; i++) {
        ThreadInfo[] tis = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
        // 只打印最后一条
//        if (i == t - 1) {
//          Agent.sendPart(ResultHelper.newFragment("---thread snapshot---"));
//          Agent.sendPart(ResultHelper.newFragment(JSON.toJSONString(tis, true)));
//        }
        // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(tis, true)));
        addThreadInfo(node,className,methodName, tis);
        Thread.sleep(perfInterval);
      }
//      Agent.sendPart(ResultHelper.newFragment(""));
//      Agent.sendPart(ResultHelper.newFragment(""));
//      Agent.sendPart(ResultHelper.newFragment(""));
      Agent.sendPart(ResultHelper.newFragment("---method cpu cost---"));
      // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(root, true)));
      this.printSubNode(node.getTimecost(), 0, node);
    }
    // 统计所有方法的耗时
    else {
      PerfTree tree = new PerfTree();
      for (int i = 0; i < t; i++) {
        ThreadInfo[] tis = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
        // 只打印最后一条
//        if (i == t - 1) {
//          Agent.sendPart(ResultHelper.newFragment("---thread snapshot---"));
//          Agent.sendPart(ResultHelper.newFragment(JSON.toJSONString(tis, true)));
//        }
        // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(tis, true)));
        addThreadInfo(tree, tis);
        Thread.sleep(perfInterval);
      }
//      Agent.sendPart(ResultHelper.newFragment(""));
//      Agent.sendPart(ResultHelper.newFragment(""));
//      Agent.sendPart(ResultHelper.newFragment(""));
      Agent.sendPart(ResultHelper.newFragment("---top cpu cost threads---"));
      // Agent.sendResult(ResultHelper.newResult(JSON.toJSONString(root, true)));
      printTree(tree);
    }

    Agent.sendResult(ResultHelper.newResult("finished!"));
  }

  private void printTree(PerfTree tree) {
    Set<Entry<String, PerfNode>> entrySet = tree.getThreadRoots().entrySet();
    Entry<String, PerfNode>[] entrys = entrySet.toArray(new Entry[entrySet.size()]);
    Arrays.sort(entrys, new Comparator<Entry<String, PerfNode>>() {

      @Override
      public int compare(Entry<String, PerfNode> o1, Entry<String, PerfNode> o2) {

        long l1 = o1.getValue().getTimecost();
        long l2 = o2.getValue().getTimecost();
        if (l2 > l1) {
          return 1;
        } else if (l2 == l1) {
          return 0;
        } else {
          return -1;
        }
      }

    });
    for (Entry<String, PerfNode> entry : entrys) {
      Agent.sendPart(ResultHelper.newFragment("线程：" + entry.getKey()));
      printSubNode(entry.getValue().getTimecost(), 0, entry.getValue());
      Agent.sendPart(ResultHelper.newFragment(""));
    }
  }

  private void printSubNode(long allCost, int indent, PerfNode currentNode) {
    Set<Entry<PerfNodeKey, PerfNode>> entrySet = currentNode.getChildren().entrySet();
    Entry<PerfNodeKey, PerfNode>[] entrys = entrySet.toArray(new Entry[entrySet.size()]);
    Arrays.sort(entrys, new Comparator<Entry<PerfNodeKey, PerfNode>>() {

      @Override
      public int compare(Entry<PerfNodeKey, PerfNode> o1, Entry<PerfNodeKey, PerfNode> o2) {

        long l1 = o1.getValue().getTimecost();
        long l2 = o2.getValue().getTimecost();
        if (l2 > l1) {
          return 1;
        } else if (l2 == l1) {
          return 0;
        } else {
          return -1;
        }
      }

    });
    for (Entry<PerfNodeKey, PerfNode> entry : entrys) {
      PerfNodeKey key = entry.getKey();
      PerfNode node = entry.getValue();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < indent; i++) {
        sb.append("--");
      }
      sb.append(key.getClassName() + "." + key.getMethodName() + " => "
          + (node.getTimecost() * 100 / allCost) + "%" + " "
          + (node.getTimecostCpu() * 100 / allCost) + "%");
      Agent.sendPart(ResultHelper.newFragment(sb.toString()));
      // 打印子节点
      printSubNode(allCost, indent + 1, node);

    }
    // 打印自用
    if (currentNode.getChildren().size() != 0) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < indent; i++) {
        sb.append("--");
      }
      sb.append("自用 => " + (currentNode.getSelfTimecost() * 100 / allCost) + "%" + " "
          + (currentNode.getSelfTimecostCpu() * 100 / allCost) + "%");
      Agent.sendPart(ResultHelper.newFragment(sb.toString()));
    }

  }


  @Override
  public void handle(Args args, CommandDispatcher dispatcher) throws InterruptedException {
    perfThread(args);
  }


  @Override
  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("thread [-t <time>] [-m <method>]");
  }

}
