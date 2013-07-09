package com.chenjw.knife.agent.service;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.core.ServiceRegistry;

/**
 * 
 * 对象引用服务，管理框架保存的业务对象的引用( 返回结果里出现的 @1 @2 那些)
 * 
 * @author chenjw
 *
 */
public class ObjectHolderService implements Lifecycle {

    private ObjectHolder holder;

    private ObjectHolder getObjectHolder() {
        return holder;
    }

    public int record(Object obj) {
        ObjectHolder r = getObjectHolder();
        return r.add(obj);
    }

    public String toId(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return "@" + record(obj) + " ";
        }
    }

    public Object get(int num) {

        ObjectHolder r = getObjectHolder();
        return r.get(num);
    }

    private static class ObjectHolder {
        private List<Object>         list = new ArrayList<Object>();

        private Map<Object, Integer> map  = new IdentityHashMap<Object, Integer>();

        public ObjectHolder() {
            ServiceRegistry.getService(SystemTagService.class).registerSystemTag(
                "SYSTEM_RECORD_LIST", list);
            ServiceRegistry.getService(SystemTagService.class).registerSystemTag(
                "SYSTEM_RECORD_MAP", map);
        }

        public void clear() {
            list.clear();
            map.clear();
        }

        public Object get(int i) {
            if (i >= list.size() || i < 0) {
                return null;
            }
            return list.get(i);
        }

        public int add(Object obj) {
            if (obj == null) {
                return -1;
            }
            Integer i = map.get(obj);
            if (i == null) {
                list.add(obj);
                i = list.size() - 1;
                map.put(obj, i);
                return i;
            }
            return i;
        }
    }

    @Override
    public void init() {
        this.holder = new ObjectHolder();
    }

    @Override
    public void clear() {
        if (holder != null) {
            holder.clear();
            holder = null;
        }
    }

    @Override
    public void close() {

    }
}
