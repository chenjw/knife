package com.chenjw.knife.core.model;

public class ReferenceListInfo {
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
