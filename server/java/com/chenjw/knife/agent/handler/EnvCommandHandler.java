package com.chenjw.knife.agent.handler;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.result.Kv;
import com.chenjw.knife.core.model.result.SystemEnvInfo;
import com.chenjw.knife.utils.StringHelper;

public class EnvCommandHandler implements CommandHandler {

  public void handle(Args args, CommandDispatcher dispatcher) {

    SystemEnvInfo envInfo = new SystemEnvInfo();
    envInfo.setSystemEnv(new HashMap<String, String>(System.getenv()));
    envInfo.setSystemProperties(System.getProperties());
    List<Kv<String, String>> vmInfo = new ArrayList<Kv<String, String>>();
    RuntimeMXBean runtimeMBean = ManagementFactory.getRuntimeMXBean();
    vmInfo.add(new Kv<String, String>("jvm name", runtimeMBean.getVmName()));
    vmInfo.add(new Kv<String, String>("lib path", runtimeMBean.getLibraryPath()));
    vmInfo.add(new Kv<String, String>("jvm start time", runtimeMBean.getStartTime()+" "+new Date(runtimeMBean.getStartTime())));
    
    vmInfo.add(new Kv<String, String>("jvm version", runtimeMBean.getVmVersion()));
    vmInfo.add(
        new Kv<String, String>("jvm args", StringHelper.join(runtimeMBean.getInputArguments(), ' ')));

    OperatingSystemMXBean osMBean =
        (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    // 获取操作系统相关信息
    vmInfo.add(new Kv<String, String>("os name", osMBean.getName()));
    vmInfo.add(new Kv<String, String>("os version", osMBean.getVersion()));
    vmInfo.add(new Kv<String, String>("os arch", osMBean.getArch()));
    vmInfo.add(new Kv<String, String>("os availableProcessors",
        String.valueOf(osMBean.getAvailableProcessors())));
    vmInfo.add(new Kv<String, String>("os systemLoadAverage",
        String.valueOf(osMBean.getSystemLoadAverage())));

    // ==========================Thread=========================

    // 获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
    ThreadMXBean threadMBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    vmInfo
        .add(new Kv<String, String>("thread count", String.valueOf(threadMBean.getThreadCount())));
    vmInfo.add(new Kv<String, String>("peak thread count",
        String.valueOf(threadMBean.getPeakThreadCount())));
    vmInfo.add(new Kv<String, String>("current thread cpu time",
        String.valueOf(threadMBean.getCurrentThreadCpuTime())));
    vmInfo.add(new Kv<String, String>("daemon thread count",
        String.valueOf(threadMBean.getDaemonThreadCount())));
    vmInfo.add(new Kv<String, String>("current thread user time",
        String.valueOf(threadMBean.getCurrentThreadUserTime())));
    // ==========================Compilation=========================

    CompilationMXBean compilMBean = (CompilationMXBean) ManagementFactory.getCompilationMXBean();
    vmInfo.add(new Kv<String, String>("compilation name", compilMBean.getName()));
    vmInfo.add(new Kv<String, String>("total compilation time",
        String.valueOf(compilMBean.getTotalCompilationTime())));
    // ==========================MemoryPool=========================

    // 获取多个内存池的使用情况
    List<MemoryPoolMXBean> mpMBeanList = ManagementFactory.getMemoryPoolMXBeans();
    List<String> memoryPoolUsage = new ArrayList<String>();
    for (MemoryPoolMXBean mpMBean : mpMBeanList) {
      memoryPoolUsage.add(StringHelper.join(mpMBean.getMemoryManagerNames() , ",")+ " : " + mpMBean.getUsage());
    }
    vmInfo
        .add(new Kv<String, String>("memory pool usage", StringHelper.join(memoryPoolUsage, " ")));
    // ==========================GarbageCollector=========================


    // ==========================Other=========================
    // Java 虚拟机中的内存总量,以字节为单位
    int total = (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;
    int free = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
    int max = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
    vmInfo.add(new Kv<String, String>("total memory", total + "mb"));
    vmInfo.add(new Kv<String, String>("free memory", free + "mb"));
    vmInfo.add(new Kv<String, String>("max memory", max + "mb"));
    envInfo.setVmInfo(vmInfo);

    Agent.sendPart(ResultHelper.newFragment(envInfo));
    Agent.sendResult(ResultHelper.newResult("env finished!"));
  }

  @Override
  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("env");
  }

}
