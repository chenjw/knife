package com.chenjw.knife.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.Printer;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.divide.BodyElement;
import com.chenjw.knife.core.model.divide.Dividable;
import com.chenjw.knife.core.model.divide.FooterFragment;
import com.chenjw.knife.core.model.divide.HeaderFragment;
import com.chenjw.knife.core.packet.ClosePacket;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.ResultPacket;
import com.chenjw.knife.core.packet.TextPacket;
import com.chenjw.knife.utils.GlobalIdHelper;

public class Agent {
	private static AgentInfo agentInfo = null;

	public static Printer printer = new Printer() {
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
			agentInfo.getInst().redefineClasses(
					new ClassDefinition(clazz, bytes));
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

	public static void sendResult(Result r) {
		if (r != null && r.isSuccess() && r.getContent() != null
				&& (r.getContent() instanceof Dividable)) {
			sendDividableResult((Dividable) r.getContent());
		} else {
			directSendResult(r);
		}
	}

	private static void sendDividableResult(Dividable dObj) {
		String id = GlobalIdHelper.getGlobalId();
		List<Object> objs = new ArrayList<Object>();
		dObj.divide(objs);
		HeaderFragment header = new HeaderFragment();
		header.setId(id);
		header.setCount(objs.size());
		header.setType(dObj.getClass().getName());
		directSendResult(ResultHelper.newResult(header));
		int i = 0;
		for (Object obj : objs) {
			BodyElement fragment = new BodyElement();
			fragment.setId(id);
			fragment.setContent(obj);
			fragment.setIndex(i);
			directSendResult(ResultHelper.newResult(fragment));
			i++;
		}
		FooterFragment footer = new FooterFragment();
		footer.setId(id);
		directSendResult(ResultHelper.newResult(footer));
	}

	public static void send(Packet command) {
		try {
			PacketResolver.write(command, agentInfo.getOs());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void directSendResult(Result result) {
		send(new ResultPacket(result));
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
