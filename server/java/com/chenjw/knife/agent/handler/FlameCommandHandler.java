package com.chenjw.knife.agent.handler;


import java.io.File;
import java.util.Map;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.flamegraph.AsyncProfiler;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;


/**
 * 基于 https://github.com/jvm-profiling-tools/async-profiler/blob/v1.8.2/src/arguments.cpp#L49
 * 
 * @author chenjw
 *
 */
public class FlameCommandHandler implements CommandHandler {

  @Override
  public void handle(Args args, CommandDispatcher dispatcher) throws Exception {

    int time = 30;
    Map<String, String> tOptions = args.option("-t");
    if (tOptions != null) {
      time = Integer.parseInt(tOptions.get("time"));
    }
    String event = null;
    Map<String, String> eOptions = args.option("-e");
    if (eOptions != null) {
      event = eOptions.get("event");
    }
    String file = "flame_graph.svg";
    Map<String, String> fOptions = args.option("-f");
    if (fOptions != null) {
      file = fOptions.get("file");
    }
    AsyncProfiler asyncProfiler = AsyncProfiler.getInstance();
    String startCmd = "start,file=" + file;
    if (event != null) {
      startCmd += "," + event;
    }
    Agent.sendPart(ResultHelper.newFragment("start sampling wait " + time + " seconds..."));
    asyncProfiler.execute(startCmd);
    Thread.sleep(time * 1000l);
    asyncProfiler.execute("stop,file="+file);
    
    Agent.sendPart(ResultHelper.newFragment("flame graph saved to " + new File(file).getAbsolutePath()));
    Agent.sendResult(ResultHelper.newResult("finished!"));
  }


  @Override
  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("flame [-t <time>] [-m <method>] [-e <event>] [-f <file>]");
  }


}
