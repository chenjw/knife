package com.chenjw.knife.agent.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.event.Event;
import com.chenjw.knife.agent.event.MethodExceptionEndEvent;
import com.chenjw.knife.agent.event.MethodReturnEndEvent;
import com.chenjw.knife.agent.event.MethodStartEvent;
import com.chenjw.knife.agent.service.InvokeDepthService;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.model.result.MethodStatInfo;
import com.chenjw.knife.core.model.result.MethodStatListInfo;

/**
 * 统计调用结果
 * 
 * @author chenjw
 *
 */
public class StatPrintFilter implements Filter {
    private Map<String, Long> times = new HashMap<String, Long>();

    protected void onStart(MethodStartEvent event) {
        String key = event.getClassName() + "." + event.getMethodName();
        Long value = times.get(key);
        if (value == null) {
            value = 1L;
        } else {
            value++;
        }
        times.put(key, value);
    }

    protected void onEnd() {
        // 表示结束了
        if (ServiceRegistry.getService(InvokeDepthService.class).getDep() == 0) {
            List<MethodStatInfo> infos = new ArrayList<MethodStatInfo>();
            for (Entry<String, Long> entry : times.entrySet()) {
                MethodStatInfo info = new MethodStatInfo();
                info.setMethod(entry.getKey());
                info.setCount(entry.getValue());
                infos.add(info);
            }
            Collections.sort(infos, new Comparator<MethodStatInfo>() {

                @Override
                public int compare(MethodStatInfo o1, MethodStatInfo o2) {
                    return (int) (o1.getCount() - o2.getCount());
                }
            });
            MethodStatListInfo listInfo = new MethodStatListInfo();
            listInfo.setMethodStatInfos(infos.toArray(new MethodStatInfo[infos.size()]));
            Agent.sendResult(ResultHelper.newResult(listInfo));
        }
    }

    @Override
    public void doFilter(Event event, FilterChain chain) throws Exception {
        if (event instanceof MethodStartEvent) {
            onStart((MethodStartEvent) event);
        } else if (event instanceof MethodReturnEndEvent) {
            onEnd();
        } else if (event instanceof MethodExceptionEndEvent) {
            onEnd();
        }
        chain.doFilter(event);
    }

}
