package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.CommandStatusService;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.ResultPart;
import com.chenjw.knife.core.packet.ClosePacket;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.ResultPacket;
import com.chenjw.knife.core.packet.ResultPartPacket;
import com.chenjw.knife.core.packet.TextPacket;

public class Agent {
    private static AgentInfo agentInfo = null;

    public static Printer    printer   = new Printer() {
                                           @Override
                                           public void info(String str) {
                                               Agent.info(str);
                                           }

                                           @Override
                                           public void debug(String str) {
                                               Agent.debug(str);
                                           }
                                       };

    public static void redefineClasses(Class<?> clazz, byte[] bytes) {
        try {
            agentInfo.getInst().redefineClasses(new ClassDefinition(clazz, bytes));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnmodifiableClassException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Class<?>[] getAllLoadedClasses() {
        try {
            return agentInfo.getInst().getAllLoadedClasses();
            // inst.addTransformer(t, false);
        } catch (Exception e) {
            e.printStackTrace();
            return new Class<?>[0];
        }
    }

    public static void sendPart(ResultPart r) {
        // if (r.getContent() != null && (r.getContent() instanceof Dividable))
        // {
        // sendDividableResult((Dividable) r.getContent());
        // } else {
        directSendPart(r);
        // }
    }

    public static void sendResult(Result r) {
        // if (r != null && r.isSuccess() && r.getContent() != null
        // && (r.getContent() instanceof Dividable)) {
        // sendDividableResult((Dividable) r.getContent());
        // } else {
        directSendResult(r);
        // }
    }

    // private static void sendDividableResult(Dividable dObj) {
    // String id = GlobalIdHelper.getGlobalId();
    // List<Object> objs = new ArrayList<Object>();
    // dObj.divide(objs);
    // HeaderFragment header = new HeaderFragment();
    // header.setId(id);
    // header.setCount(objs.size());
    // header.setType(dObj.getClass().getName());
    // directSendResult(ResultHelper.newResult(header));
    // int i = 0;
    // for (Object obj : objs) {
    // BodyElement fragment = new BodyElement();
    // fragment.setId(id);
    // fragment.setContent(obj);
    // fragment.setIndex(i);
    // directSendResult(ResultHelper.newResult(fragment));
    // i++;
    // }
    // FooterFragment footer = new FooterFragment();
    // footer.setId(id);
    // directSendResult(ResultHelper.newResult(footer));
    // }

    public static void send(Packet command) {
        try {
            if (agentInfo != null) {
                PacketResolver.write(command, agentInfo.getOs());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void directSendResult(Result result) {
        Command c = ServiceRegistry.getService(CommandStatusService.class).getCurrentCommand();
        if (c == null) {
            return;
        }
        result.setRequestId(c.getId());
        send(new ResultPacket(result));
        ServiceRegistry.getService(CommandStatusService.class).setCurrentCommand(null);
    }

    private static void directSendPart(ResultPart result) {
        Command c = ServiceRegistry.getService(CommandStatusService.class).getCurrentCommand();
        if (c == null) {
            return;
        }
        result.setRequestId(c.getId());
        send(new ResultPartPacket(result));
    }

    public static boolean isDebugOn() {
        if (agentInfo == null) {
            return false;
        } else {
            return agentInfo.isDebugOn();
        }

    }

    public static void info(String msg) {
        send(new TextPacket(msg));
    }

    public static void debug(String msg) {
        if (isDebugOn()) {
            info("[DEBUG] " + msg);
        }
    }

    public static void close() {
        try {
            send(new ClosePacket());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            agentInfo.getSocket().close();
        } catch (Throwable e) {
        }
        agentInfo = null;
    }

    public static void setAgentInfo(AgentInfo info) {
        Agent.agentInfo = info;
    }

    public static AgentInfo getAgentInfo() {
        return agentInfo;
    }

}
