package com.chenjw.knife.agent.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.CommandHandler;
import com.chenjw.knife.agent.Context;
import com.chenjw.knife.agent.NativeHelper;
import com.chenjw.knife.agent.handler.constants.Constants;

public class LsCommandHandler implements CommandHandler {

	public void handle(String[] args) {
		try {
			Object obj = Context.get(Constants.THIS);
			if (obj == null) {
				Agent.print("not found!");
				return;
			}
			Map<Field, Object> fieldMap = NativeHelper.getFieldValues(obj);
			List<Object> list = new ArrayList<Object>();
			int i = 0;
			for (Entry<Field, Object> entry : fieldMap.entrySet()) {
				Agent.print(i + ". " + entry.getKey().getName() + "="
						+ entry.getValue());
				list.add(entry.getValue());
				i++;
			}
			Context.put(Constants.LIST, list.toArray(new Object[list.size()]));
			Agent.print("find " + i + " fields of " + obj);
		} catch (Exception e) {
			e.printStackTrace();
			Agent.print(e.getClass().getName() + ":" + e.getLocalizedMessage());
		}
	}

	@Override
	public String getName() {
		return "ls";
	}
}
