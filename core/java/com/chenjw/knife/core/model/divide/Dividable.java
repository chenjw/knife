package com.chenjw.knife.core.model.divide;

import java.util.List;

public interface Dividable {
	public void divide(List<Object> fragments);

	public void combine(Object[] fragments);
}
