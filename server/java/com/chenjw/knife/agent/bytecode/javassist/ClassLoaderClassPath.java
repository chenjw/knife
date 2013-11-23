package com.chenjw.knife.agent.bytecode.javassist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javassist.ClassPath;

import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ByteCodeService;

/**
 * 
 * 
 * @author chenjw
 *
 */
public class ClassLoaderClassPath implements ClassPath {
	private ClassLoader classLoader;

	public ClassLoaderClassPath(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void close() {
	}

	public String toString() {
		return "classLoader:" + classLoader;
	}

	public InputStream openClassfile(String classname) {
	    if(classLoader==null){
	        return null;
	    }
		Class<?> clazz;
		try {

			clazz = classLoader.loadClass(classname);
		} catch (ClassNotFoundException e) {
			System.out.println(classLoader + " load " + classname + " fail");
			return null;
		}
		if (clazz == null) {
			return null;
		}
		return new ByteArrayInputStream(ServiceRegistry.getService(
				ByteCodeService.class).getByteCode(clazz));

	}

	public URL find(String classname) {
		Class<?> clazz;
		try {
			clazz = classLoader.loadClass(classname);
		} catch (ClassNotFoundException e) {
			return null;
		}
		if (clazz == null) {
			return null;
		}
		String cname = classname.replace('.', '/') + ".class";
		try {
			return new URL("file:/ClassLoaderClassPath/" + cname);
		} catch (MalformedURLException e) {
			return null;
		}
	}

}
