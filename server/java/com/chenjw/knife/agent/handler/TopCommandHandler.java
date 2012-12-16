package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.NativeHelper.ReferenceCount;
import com.chenjw.knife.agent.utils.OSHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.model.ObjectInfo;
import com.chenjw.knife.core.model.ReferenceCountInfo;
import com.chenjw.knife.core.model.ThreadInfo;
import com.chenjw.knife.core.model.TopReferenceCountInfo;
import com.chenjw.knife.core.model.TopThreadInfo;

public class TopCommandHandler implements CommandHandler {
	private void handleRef(Args args, CommandDispatcher dispatcher) {
		int num = 10;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			num = Integer.parseInt(nOptions.get("num"));
		}
		TopReferenceCountInfo info = new TopReferenceCountInfo();
		List<ReferenceCountInfo> referenceInfos = new ArrayList<ReferenceCountInfo>();
		for (ReferenceCount referenceCount : NativeHelper.countReferree(num)) {
			ReferenceCountInfo referenceInfo = new ReferenceCountInfo();
			referenceInfo.setCount(referenceCount.getCount());
			ObjectInfo obj = new ObjectInfo();
			obj.setObjectId(ServiceRegistry.getService(
					ObjectHolderService.class).toId(referenceCount.getObj()));
			obj.setValueString(ToStringHelper.toString(referenceCount.getObj()));
			referenceInfo.setObj(obj);
			referenceInfos.add(referenceInfo);
		}
		info.setReferenceCounts(referenceInfos
				.toArray(new ReferenceCountInfo[referenceInfos.size()]));
		Agent.sendResult(ResultHelper.newResult(info));
	}

	private void handleThread(Args args, CommandDispatcher dispatcher) {
		int num = 10;
		Map<String, String> nOptions = args.option("-n");
		if (nOptions != null) {
			num = Integer.parseInt(nOptions.get("num"));
		}
		TopThreadInfo info = new TopThreadInfo();
		List<ThreadInfo> threadInfos = OSHelper.findTopThread(num);
		info.setThreads(threadInfos.toArray(new ThreadInfo[threadInfos.size()]));
		Agent.sendResult(ResultHelper.newResult(info));
	}

	public void handle(Args args, CommandDispatcher dispatcher) {
		String type = args.arg("type");
		if ("ref".equals(type)) {
			handleRef(args, dispatcher);
		} else if ("thread".equals(type)) {
			handleThread(args, dispatcher);
		}
	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("top [-n <num>] [<type>]");

	}

}
