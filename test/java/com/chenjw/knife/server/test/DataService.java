package com.chenjw.knife.server.test;

public interface DataService {
	public ApplyModel find(int id);

	public void insert(ApplyModel apply);

	public void update(ApplyModel apply);

}
