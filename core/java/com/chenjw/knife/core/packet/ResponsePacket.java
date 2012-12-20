package com.chenjw.knife.core.packet;

import com.chenjw.knife.core.model.Response;

public class ResponsePacket extends ObjectPacket<Response> {


	private static final long serialVersionUID = 8385303687051837614L;

	public ResponsePacket(long requestId, Object... context) {
		Response r = new Response();
		r.setRequestId(requestId);
		r.setContext(context);
		this.setObject(r);
	}

	public ResponsePacket() {
	}

}
