package com.chenjw.knife.core;

import java.io.UnsupportedEncodingException;

public class TextPacket implements Packet {
	static final byte CODE = 0;
	private String message;

	public TextPacket(String message) {
		this.message = message;
	}

	public TextPacket() {
	}

	@Override
	public byte getCode() {
		return CODE;
	}

	@Override
	public byte[] toBytes() {
		if (message == null) {
			return new byte[0];
		} else {
			try {
				return message.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				return new byte[0];
			}
		}
	}

	@Override
	public void fromBytes(byte[] bytes) {
		try {
			this.message = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
	}

	@Override
	public String toString() {
		return message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
