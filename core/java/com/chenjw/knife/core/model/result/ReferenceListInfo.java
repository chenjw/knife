package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class ReferenceListInfo implements Serializable {

	private static final long serialVersionUID = 8623272406173416394L;

	private boolean isReferree;

	private ObjectInfo[] references;

	public boolean isReferree() {
		return isReferree;
	}

	public void setReferree(boolean isReferree) {
		this.isReferree = isReferree;
	}

	public ObjectInfo[] getReferences() {
		return references;
	}

	public void setReferences(ObjectInfo[] references) {
		this.references = references;
	}

}
