package com.chenjw.knife.testgt;

import java.util.List;

public class TestClassInfo {
	private String className;
	private List<FieldInfo> mockFields;
	private List<FieldInfo> invokeFields;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<FieldInfo> getMockFields() {
		return mockFields;
	}

	public void setMockFields(List<FieldInfo> mockFields) {
		this.mockFields = mockFields;
	}

	public List<FieldInfo> getInvokeFields() {
		return invokeFields;
	}

	public void setInvokeFields(List<FieldInfo> invokeFields) {
		this.invokeFields = invokeFields;
	}

}
