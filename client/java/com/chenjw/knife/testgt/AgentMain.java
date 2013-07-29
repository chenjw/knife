package com.chenjw.knife.testgt;

import java.lang.instrument.Instrumentation;

public class AgentMain {

	public static void premain(String arguments, Instrumentation inst)
			throws Exception {
		inst.addTransformer(new Transformer());
	}
}
