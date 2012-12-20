package com.chenjw.knife.core.packet;

import java.util.concurrent.atomic.AtomicLong;

import com.chenjw.knife.core.model.Request;

public class RequestPacket extends ObjectPacket<Request> {

	private static final long serialVersionUID = 5520338838357202392L;
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
