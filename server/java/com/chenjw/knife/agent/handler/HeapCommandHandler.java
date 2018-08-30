package com.chenjw.knife.agent.handler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.GcService;
import com.chenjw.knife.agent.service.HeapHistogramService;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.result.GcInfo;
import com.chenjw.knife.core.model.result.HeapClassInfo;
import com.chenjw.knife.core.model.result.HeapHistogram;


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
      //Agent.sendPart(ResultHelper.newFragment(JSON.toJSONString(gcInfo, true)));
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
    hh.setFgcIncrement(hh.getFgc() - base.getFgc());
    hh.setYgcIncrement(hh.getYgc() - base.getYgc());
    hh.setMemHeapUsedIncrement(hh.getMemHeapUsed() - base.getMemHeapUsed());
    hh.setMemNonHeapUsedIncrement(hh.getMemNonHeapUsed() - base.getMemNonHeapUsed());
  }

  private void computeIncrement(HeapHistogram base, HeapHistogram hh) {
    // 计算新增加的
    for (Entry<String, HeapClassInfo> entry : hh.getTempClassesMap().entrySet()) {
      HeapClassInfo baseHci = base.getTempClassesMap().get(entry.getKey());
      HeapClassInfo newHci = entry.getValue();
      if (baseHci != null) {
        newHci.setBytesIncrement(newHci.getBytes() - baseHci.getBytes());
        newHci.setInstancesCountIncrement(newHci.getInstancesCount() - baseHci.getInstancesCount());
      } else {
        newHci.setBytesIncrement(newHci.getBytes());
        newHci.setInstancesCountIncrement(newHci.getInstancesCount());
      }
    }
    hh.setTotalBytesIncrement(hh.getTotalBytes() - base.getTotalBytes());
    hh.setTotalInstancesIncrement(hh.getTotalInstances() - base.getTotalInstances());
  }

  private void prepareComputeChange(HeapHistogram hh) {
    Map<String, HeapClassInfo> tempMap = new HashMap<String, HeapClassInfo>();
    for (HeapClassInfo c : hh.getClasses()) {
      tempMap.put(c.getName(), c);
    }
    hh.setTempClassesMap(tempMap);
  }

  @Override
  public void handle(Args args, CommandDispatcher dispatcher)
      throws IOException, InterruptedException {
    Map<String, String> nOptions = args.option("-gc");
    if (nOptions != null) {
      showGc();
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
        long b1 = sortByIncrement ? o1.getBytesIncrement() : o1.getBytes();
        long b2 = sortByIncrement ? o2.getBytesIncrement() : o2.getBytes();
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


  @Override
  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("heap [-i] [-gc] [-t <time>] [-n <topnum>]");
  }

}
