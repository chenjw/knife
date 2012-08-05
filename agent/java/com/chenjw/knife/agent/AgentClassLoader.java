package com.chenjw.knife.agent;

import java.net.URL;
import java.net.URLClassLoader;

public class AgentClassLoader extends URLClassLoader {

	public AgentClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

}
