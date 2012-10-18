package com.chenjw.knife.agent.handler;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.args.ArgDef;
import com.chenjw.knife.agent.args.Args;
import com.chenjw.knife.agent.constants.Constants;
import com.chenjw.knife.agent.core.CommandDispatcher;
import com.chenjw.knife.agent.core.CommandHandler;
import com.chenjw.knife.agent.formater.PreparedTableFormater;
import com.chenjw.knife.agent.manager.ContextManager;
import com.chenjw.knife.agent.manager.ObjectRecordManager;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.agent.utils.ToStringHelper;
import com.chenjw.knife.core.Printer.Level;
import com.chenjw.knife.utils.StringHelper;

public class FindCommandHandler implements CommandHandler {

	private Class<?> findClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private Class<?>[] findLikeClass(String className) {
		List<Class<?>> likeClass = new ArrayList<Class<?>>();
		for (Class<?> clazz : Agent.getAllLoadedClasses()) {
			if (clazz.getName().indexOf(className) != -1) {
				likeClass.add(clazz);
			}
		}
		return likeClass.toArray(new Class<?>[likeClass.size()]);
	}

	private boolean isNumeric(String str) {
		return StringHelper.isNumeric(str);
	}

	public void handle(Args args, CommandDispatcher dispatcher) {
		Class<?> clazz = null;
		String className = args.arg("find-expretion");
		if (isNumeric(className)) {
			Class<?>[] likeClazz = (Class<?>[]) ContextManager.getInstance()
					.get(Constants.CLASS_LIST);
			clazz = likeClazz[Integer.parseInt(className)];
		} else {
			clazz = findClass(className);
			if (clazz == null) {
				Class<?>[] likeClazz = findLikeClass(className);
				if (likeClazz.length > 1) {
					ContextManager.getInstance().put(Constants.CLASS_LIST,
							likeClazz);

					int i = 0;
					PreparedTableFormater table = new PreparedTableFormater(
							Level.INFO, Agent.printer, args.getGrep());

					table.setTitle("idx", "type", "name", "classloader");
					for (Class<?> cc : likeClazz) {
						table.addLine(String.valueOf(i),
								cc.isInterface() ? "[interface]" : "[class]",
								cc.getName(),
								"[" + ToStringHelper.toClassLoaderString(cc)
										+ "]");
						i++;
					}
					table.print();
					Agent.info("find " + i + " classes like '" + className
							+ "', please choose one typing like 'find 0'!");
					return;
				} else if (likeClazz.length == 1) {
					clazz = likeClazz[0];
				}
			}
			if (clazz == null) {
				Agent.info("not found!");
				return;
			}
		}
		// //
		Object[] objs = NativeHelper.findInstancesByClass(clazz);

		int i = 0;
		PreparedTableFormater table = new PreparedTableFormater(Level.INFO,
				Agent.printer, args.getGrep());

		table.setTitle("type", "obj-id", "detail");
		for (Object obj : objs) {
			table.addLine("[instance]",
					ObjectRecordManager.getInstance().toId(obj),
					ToStringHelper.toDetailString(obj));
			i++;
		}
		table.print();
		Agent.info("find " + i + " instances of " + clazz.getName());
	}

	public void declareArgs(ArgDef argDef) {
		argDef.setCommandName("find");
		argDef.setDef("<find-expretion>");
		argDef.setDesc("find classes and instances from the heap");
		argDef.addOptionDesc("find-expretion", "className or the object id.");
	}
}
