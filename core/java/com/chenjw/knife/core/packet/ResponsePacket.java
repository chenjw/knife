package com.chenjw.knife.core.packet;

public class ResponsePacket extends ObjectPacket<Response> {

	public ResponsePacket(long requestId, Object... context) {
		Response r = new Response();
		r.setRequestId(requestId);
		r.setContext(context);
		this.setObject(r);
	}

	public ResponsePacket() {
	}

}
