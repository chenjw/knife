package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class LogCommandHandler implements CommandHandler {

  public void handle(Args args, CommandDispatcher dispatcher) {

    for (int i = 0; i < 10; i++) {
      Agent.clearConsole();
      Agent.sendPart(ResultHelper.newFragment(i * 10 + "% finished!"));
      Agent.sendPart(ResultHelper.newFragment((100-i * 10) + "% remain!"));
      try {
        Thread.sleep(1000l);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    Agent.sendResult(ResultHelper.newResult("log finished!"));
  }

  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("log");

  }
}
