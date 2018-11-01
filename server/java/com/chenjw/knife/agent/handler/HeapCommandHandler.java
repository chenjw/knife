package com.chenjw.knife.agent.handler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.filter.HeapNewStatFilter;
import com.chenjw.knife.agent.filter.CurrentContextClassLoaderFilter;
import com.chenjw.knife.agent.filter.ExceptionFilter;
import com.chenjw.knife.agent.filter.Filter;
import com.chenjw.knife.agent.filter.FilterInvocationListener;
import com.chenjw.knife.agent.filter.SystemOperationFilter;
import com.chenjw.knife.agent.handler.perf.HeapProfilerNode;
import com.chenjw.knife.agent.handler.perf.HeapProfilerNodeKey;
import com.chenjw.knife.agent.handler.perf.HeapProfilerTree;
import com.chenjw.knife.agent.service.ByteCodeService;
import com.chenjw.knife.agent.service.GcService;
import com.chenjw.knife.agent.service.HeapHistogramService;
import com.chenjw.knife.agent.service.HeapProfilerService;
import com.chenjw.knife.agent.service.InstrumentService;
import com.chenjw.knife.agent.utils.ClassLoaderHelper;
import com.chenjw.knife.agent.utils.CommandHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.CommandHelper.ClassOrObject;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.result.GcInfo;
import com.chenjw.knife.core.model.result.HeapClassInfo;
import com.chenjw.knife.core.model.result.HeapHistogram;
import com.chenjw.knife.core.model.result.LongValue;
import com.chenjw.knife.utils.StringHelper;


public class HeapCommandHandler implements CommandHandler {
  private long perfInterval = 1000;

  private void showGc() {
    GcService gcService = ServiceRegistry.getService(GcService.class);
    // 计算gc
    GcInfo gcInfo = gcService.collectGcInfo();
    Agent.sendPart(ResultHelper.newFragment(JSON.toJSONString(gcInfo, true)));
  }

  private void perfHeapIncrement(Args args) throws IOException, InterruptedException {
    HeapHistogramService heapHistogramService =
        ServiceRegistry.getService(HeapHistogramService.class);
    GcService gcService = ServiceRegistry.getService(GcService.class);
    // 默认perf5秒
    int second = 5;
    Map<String, String> nOptions = args.option("-t");
    if (nOptions != null) {
      second = Integer.parseInt(nOptions.get("time"));
    }
    //
    int t = (int) (second * 1000 / perfInterval);
    HeapHistogram baseHh = null;
    GcInfo baseGcInfo = null;
    for (int i = 0; i < t; i++) {
      Agent.clearConsole();
      // 计算gc
      GcInfo gcInfo = gcService.collectGcInfo();
      // Agent.sendPart(ResultHelper.newFragment(JSON.toJSONString(gcInfo, true)));
      if (baseGcInfo == null) {
        baseGcInfo = gcInfo;
      } else {
        computeIncrement(baseGcInfo, gcInfo);
      }
      // 计算heap
      HeapHistogram hh = heapHistogramService.getHeapHistogram();
      prepareComputeChange(hh);
      if (baseHh == null) {
        baseHh = hh;
      } else {
        computeIncrement(baseHh, hh);
        // 排序
        sortAndTruncat(args, hh);

        Agent.sendPart(ResultHelper.newFragment(hh));
      }
      Agent.sendPart(ResultHelper.newFragment(gcInfo));
      Thread.sleep(perfInterval);
    }
    // System.out.println(JSON.toJSONString(hh, true));

  }

  private void computeIncrement(GcInfo base, GcInfo hh) {
    // 计算新增加的
    computeIncrement(base.getFgc(), hh.getFgc());
    computeIncrement(base.getYgc(), hh.getYgc());
    computeIncrement(base.getMemHeapUsed(), hh.getMemHeapUsed());
    computeIncrement(base.getMemNonHeapUsed(), hh.getMemNonHeapUsed());
  }

  private void computeIncrement(LongValue base, LongValue forCompute) {
    long increment = forCompute.getValue() - base.getValue();
    long max = Math.max(base.getMax(), forCompute.getValue());
    long min = Math.min(base.getMin(), forCompute.getValue());
    forCompute.setIncrement(increment);
    forCompute.setMax(max);
    forCompute.setMin(min);
    base.setMax(max);
    base.setMin(min);
  }



  private void computeIncrement(HeapHistogram base, HeapHistogram hh) {
    // 计算新增加的
    for (Entry<String, HeapClassInfo> entry : hh.getTempClassesMap().entrySet()) {
      HeapClassInfo baseHci = base.getTempClassesMap().get(entry.getKey());
      HeapClassInfo newHci = entry.getValue();
      if (baseHci == null) {
        baseHci = new HeapClassInfo();
        baseHci.setName(entry.getKey());
        base.getTempClassesMap().put(entry.getKey(), baseHci);
      }
      computeIncrement(baseHci.getBytes(), newHci.getBytes());
      computeIncrement(baseHci.getInstancesCount(), newHci.getInstancesCount());
    }
    computeIncrement(base.getTotalBytes(), hh.getTotalBytes());
    computeIncrement(base.getTotalInstances(), hh.getTotalInstances());

  }

  private void prepareComputeChange(HeapHistogram hh) {
    Map<String, HeapClassInfo> tempMap = new HashMap<String, HeapClassInfo>();
    for (HeapClassInfo c : hh.getClasses()) {
      tempMap.put(c.getName(), c);
    }
    hh.setTempClassesMap(tempMap);
  }

  @Override
  public void handle(Args args, CommandDispatcher dispatcher) throws Exception {
    Map<String, String> gcOptions = args.option("-gc");
    Map<String, String> pOptions = args.option("-p");
    if (gcOptions != null) {
      showGc();
    } else if (pOptions != null) {
      profileClass(args);
    } else {
      perfHeapIncrement(args);
    }
    Agent.sendResult(ResultHelper.newResult("finished!"));
  }



  private void sortAndTruncat(Args args, HeapHistogram hh) {

    Map<String, String> nOptions = args.option("-i");
    final boolean sortByIncrement = nOptions != null;
    // 增量排序
    if (sortByIncrement) {
      Agent.sendPart(ResultHelper.newFragment(
          "top " + getN(args) + " increment-bytes classes in heap (use -n to set topnum) "));
      Agent.sendPart(ResultHelper.newFragment(""));
    }
    // 全量排序
    else {
      Agent.sendPart(ResultHelper
          .newFragment("top " + getN(args) + " bytes classes in heap (use -n to set topnum) "));
      Agent.sendPart(ResultHelper.newFragment(""));
    }

    Collections.sort(hh.getClasses(), new Comparator<HeapClassInfo>() {

      @Override
      public int compare(HeapClassInfo o1, HeapClassInfo o2) {
        long b1 = sortByIncrement ? o1.getBytes().getIncrement() : o1.getBytes().getIncrement();
        long b2 = sortByIncrement ? o2.getBytes().getIncrement() : o2.getBytes().getIncrement();
        if (b2 > b1) {
          return 1;
        } else if (b2 == b1) {
          return 0;
        } else {
          return -1;
        }
      }

    });
    int num = getN(args);
    List<HeapClassInfo> newList = new ArrayList<HeapClassInfo>();
    for (int i = 0; i < Math.min(num, hh.getClasses().size()); i++) {
      newList.add(hh.getClasses().get(i));
    }
    hh.setClasses(newList);

  }

  private int getN(Args args) {
    int num = 20;
    Map<String, String> nOptions = args.option("-n");
    if (nOptions != null) {
      num = Integer.parseInt(nOptions.get("topnum"));
    }
    return num;
  }


  public void profileClass(Args args) throws Exception {
    Map<String, String> pOptions = args.option("-p");
    String classnames = pOptions.get("classnames");

    List<Class> clazzes = new ArrayList<Class>();
    for (String classname : StringHelper.split(classnames, ',')) {
      ClassOrObject classInfo = CommandHelper.findTarget(classname);
      if (classInfo.getClazz() != null) {
        clazzes.add(classInfo.getClazz());
      }

    }
    if (clazzes.isEmpty()) {
      Agent.sendResult(ResultHelper.newErrorResult("class not found"));
      return;
    }
    HeapProfilerTree tree = ServiceRegistry.getService(HeapProfilerService.class).startProfile();
    // configStrategy(args, methodInfo);
    // System.out.println(JSON.toJSONString(constructors));
    configStrategy(args);


    instrument(clazzes);
    // 默认perf5秒
    int second = 5;
    Map<String, String> nOptions = args.option("-t");
    if (nOptions != null) {
      second = Integer.parseInt(nOptions.get("time"));
    }
    Thread.sleep(second * 1000l);
    printTree(tree);
    // System.out.println(JSON.toJSONString(tree, true));
    // 恢复
    ServiceRegistry.getService(ByteCodeService.class).recoverAll();
    ServiceRegistry.getService(InstrumentService.class).clear();
  }


  private void printTree(HeapProfilerTree tree) {
    Set<Entry<String, HeapProfilerNode>> entrySet = tree.getClassRoots().entrySet();
    Entry<String, HeapProfilerNode>[] entrys = entrySet.toArray(new Entry[entrySet.size()]);
    Arrays.sort(entrys, new Comparator<Entry<String, HeapProfilerNode>>() {

      @Override
      public int compare(Entry<String, HeapProfilerNode> o1, Entry<String, HeapProfilerNode> o2) {

        long l1 = o1.getValue().getBytes().get();
        long l2 = o2.getValue().getBytes().get();
        if (l2 > l1) {
          return 1;
        } else if (l2 == l1) {
          return 0;
        } else {
          return -1;
        }
      }

    });
    for (Entry<String, HeapProfilerNode> entry : entrys) {
      Agent.sendPart(
          ResultHelper.newFragment("对象：" + entry.getKey() + " " + entry.getValue().getBytes()));
      printSubNode(entry.getValue().getBytes().get(), 0, entry.getValue());
      Agent.sendPart(ResultHelper.newFragment(""));
    }
  }

  private void printSubNode(long allBytes, int indent, HeapProfilerNode currentNode) {
    Set<Entry<HeapProfilerNodeKey, HeapProfilerNode>> entrySet =
        currentNode.getChildren().entrySet();
    Entry<HeapProfilerNodeKey, HeapProfilerNode>[] entrys =
        entrySet.toArray(new Entry[entrySet.size()]);
    Arrays.sort(entrys, new Comparator<Entry<HeapProfilerNodeKey, HeapProfilerNode>>() {

      @Override
      public int compare(Entry<HeapProfilerNodeKey, HeapProfilerNode> o1,
          Entry<HeapProfilerNodeKey, HeapProfilerNode> o2) {

        long l1 = o1.getValue().getBytes().get();
        long l2 = o2.getValue().getBytes().get();
        if (l2 > l1) {
          return 1;
        } else if (l2 == l1) {
          return 0;
        } else {
          return -1;
        }
      }

    });
    for (Entry<HeapProfilerNodeKey, HeapProfilerNode> entry : entrys) {
      HeapProfilerNodeKey key = entry.getKey();
      HeapProfilerNode node = entry.getValue();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < indent; i++) {
        sb.append("--");
      }
      sb.append(key.getClassName() + "." + key.getMethodName() + " => "
          + (node.getBytes().get() * 100 / allBytes) + "%" + " " + node.getBytes());
      Agent.sendPart(ResultHelper.newFragment(sb.toString()));
      // 打印子节点
      printSubNode(allBytes, indent + 1, node);

    }


  }


  private void instrument(List<Class> clazzes) {


    // ClassLoaderHelper.resetClassLoader(clazzes.get(0));
    try {
      for (Class clazz : Agent.getAllLoadedClasses()) {
        // if (AgentClassLoader.getAgentClassLoader().isLoadByAgent(clazz)) {
        // continue;
        // }
        // System.out.println(clazzes);
        ServiceRegistry.getService(InstrumentService.class).buildNewExpr(clazz, clazzes);
      }
      // ServiceRegistry.getService(InstrumentService.class).buildConstructorEnterLeave(clazz);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void configStrategy(Args args) throws Exception {

    List<Filter> filters = new ArrayList<Filter>();
    filters.add(new SystemOperationFilter());
    // filters.add(new SystemOperationFilter());
    filters.add(new ExceptionFilter());
    filters.add(new CurrentContextClassLoaderFilter());
    // filters.add(new InstrumentClassLoaderFilter());
    filters.add(new HeapNewStatFilter());

    Profiler.listener = new FilterInvocationListener(filters);
  }

  @Override
  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("heap [-i] [-gc] [-t <time>] [-p <classnames>] [-n <topnum>]");
  }

}
