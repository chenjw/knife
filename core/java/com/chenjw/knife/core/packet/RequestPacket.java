package com.chenjw.knife.core.packet;

import java.util.concurrent.atomic.AtomicLong;

public class RequestPacket extends ObjectPacket<Request> {
	private static AtomicLong id = new AtomicLong(0);

	public RequestPacket(Object... obj) {
		Request r = new Request();
		r.setId(id.incrementAndGet());
		r.setContext(obj);
		this.setObject(r);
	}

	public RequestPacket() {
	}

}
