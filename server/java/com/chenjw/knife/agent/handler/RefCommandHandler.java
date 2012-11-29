package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ResultHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.model.ObjectInfo;
import com.chenjw.knife.core.model.ReferenceListInfo;
import com.chenjw.knife.core.result.Result;

public class RefCommandHandler implements CommandHandler {

	public void handle(Args args, CommandDispatcher dispatcher) {

		String param = args.arg("object-id");
		Object obj = null;
		if (param == null) {
			obj = ContextManager.getInstance().get(Constants.THIS);
		} else {
			int id = Integer.parseInt(param);
			obj = ObjectRecordManager.getInstance().get(id);
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
			reference.setObjectId(ObjectRecordManager.getInstance().toId(ref));
			reference.setValueString(ToStringHelper.toString(ref));
			references.add(reference);

		}
		info.setReferences(references.toArray(new ObjectInfo[references.size()]));

		Result<ReferenceListInfo> result = new Result<ReferenceListInfo>();
		result.setContent(info);
		result.setSuccess(true);
		Agent.sendResult(result);

	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("ref");
		argDef.setDef("[-r] <object-id>");
		argDef.setDesc("find referrers or referrees of target object.");
		argDef.addOptionDesc("object-id", "a num as the object id.");
		argDef.addOptionDesc("-r", "find referree not Referrer");
	}
}
