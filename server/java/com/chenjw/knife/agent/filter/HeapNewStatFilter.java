package com.chenjw.knife.agent.filter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.NewArrayEvent;
import com.chenjw.knife.agent.event.NewObjectEvent;
import com.chenjw.knife.agent.service.HeapProfilerService;

/**
 * 打印调用结果
 * 
 * @author chenjw
 *
 */
public class HeapNewStatFilter implements Filter {



  protected void onNewObject(NewObjectEvent event) {
    long objectSize = Agent.getObjectSize(event.getThisObject());
    ServiceRegistry.getService(HeapProfilerService.class).onNewElement(event.getClassName(),
        objectSize);
  }

  protected void onNewArray(NewArrayEvent event) {
    String className = event.getThisObject().getClass().getName();
    long objectSize = Agent.getObjectSize(event.getThisObject());
    ServiceRegistry.getService(HeapProfilerService.class).onNewElement(className, objectSize);
  }

  @Override
  public void doFilter(Event event, FilterChain chain) throws Exception {

    if (event instanceof NewObjectEvent) {
      onNewObject((NewObjectEvent) event);
    } else if (event instanceof NewArrayEvent) {
      onNewArray((NewArrayEvent) event);
    }
    chain.doFilter(event);
  }

}
