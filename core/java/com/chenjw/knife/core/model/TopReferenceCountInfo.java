package com.chenjw.knife.core.model;

import java.io.Serializable;

public class TopReferenceCountInfo implements Serializable {

	private static final long serialVersionUID = 2258887004048244278L;
	private ReferenceCountInfo[] referenceCounts;

	public ReferenceCountInfo[] getReferenceCounts() {
		return referenceCounts;
	}

	public void setReferenceCounts(ReferenceCountInfo[] references) {
		this.referenceCounts = references;
	}

}
