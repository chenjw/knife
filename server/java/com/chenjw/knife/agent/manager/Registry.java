package com.chenjw.knife.agent.manager;

import java.util.ArrayList;
import java.util.List;

public class Registry implements Lifecycle {
	private static final Registry INSTANCE = new Registry();

	private List<Lifecycle> services = new ArrayList<Lifecycle>();
	{
		register(ByteCodeManager.getInstance());
		register(ContextManager.getInstance());
		register(HistoryManager.getInstance());
		register(InstrumentManager.getInstance());
		register(InvokeDepthManager.getInstance());
		register(ObjectRecordManager.getInstance());
		register(PrinterManager.getInstance());
		register(SystemTagManager.getInstance());
		register(TimingManager.getInstance());
	}

	public static Registry getInstance() {
		return INSTANCE;
	}

	public void register(Lifecycle service) {
		services.add(service);
	}

	@Override
	public void init() {
		for (Lifecycle service : services) {
			service.init();
		}

	}

	@Override
	public void clear() {
		for (Lifecycle service : services) {
			service.clear();
		}

	}

	@Override
	public void close() {
		for (Lifecycle service : services) {
			service.close();
		}
	}

}
