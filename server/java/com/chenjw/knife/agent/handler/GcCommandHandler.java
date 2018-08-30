package com.chenjw.knife.agent.handler;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;

public class GcCommandHandler implements CommandHandler {

  @Override
  public void handle(Args args, CommandDispatcher dispatcher) {
    System.gc();
    Agent.sendResult(ResultHelper.newResult("gc finished!"));
  }

  public void declareArgs(ArgDef argDef) {
    argDef.setDefinition("gc");
  }

}
