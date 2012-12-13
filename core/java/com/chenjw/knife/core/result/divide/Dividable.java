package com.chenjw.knife.core.result.divide;

import java.util.List;

public interface Dividable {
	public void divide(List<Object> fragments);

	public void combine(Object[] fragments);
}
