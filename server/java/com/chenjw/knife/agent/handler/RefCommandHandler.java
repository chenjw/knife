package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ContextService;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.args.ArgDef;
import com.chenjw.knife.core.args.Args;
import com.chenjw.knife.core.model.result.ObjectInfo;
import com.chenjw.knife.core.model.result.ReferenceListInfo;

public class RefCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if (param == null) {
			obj = ServiceRegistry.getService(ContextService.class).get(
					Constants.THIS);
		} else {
			int id = Integer.parseInt(param);
			obj = ServiceRegistry.getService(ObjectHolderService.class).get(id);
		}

		if (obj == null) {
			Agent.sendResult(ResultHelper.newErrorResult("id not found!"));
			return;
		}
		Object[] refs = null;
		if (args.option("-r") != null) {
			refs = NativeHelper.findReferreeByObject(obj);
		} else {
			refs = NativeHelper.findReferrerByObject(obj);
		}

		if (refs == null || refs.length == 0) {
			Agent.sendResult(ResultHelper.newErrorResult("not found!"));
			return;
		}

		ReferenceListInfo info = new ReferenceListInfo();
		info.setReferree(args.option("-r") != null);
		List<ObjectInfo> references = new ArrayList<ObjectInfo>();
		for (Object ref : refs) {
			ObjectInfo reference = new ObjectInfo();
			reference.setObjectId(ServiceRegistry.getService(
					ObjectHolderService.class).toId(ref));
			reference.setValueString(ToStringHelper.toString(ref));
			references.add(reference);

		}
		info.setReferences(references.toArray(new ObjectInfo[references.size()]));

		Agent.sendResult(ResultHelper.newResult(info));

	}

	public void declareArgs(ArgDef argDef) {

		argDef.setDefinition("ref [-r] <object-id>");

	}
}
