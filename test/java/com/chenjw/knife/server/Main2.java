package com.chenjw.knife.server;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import com.chenjw.knife.server.test.NewClassLoader;

public class Main2 {
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, MalformedURLException {
	    NewClassLoader classLoader=new NewClassLoader();
	    Thread.currentThread().setContextClassLoader(classLoader);
	    Class<?> clazz=classLoader.loadClass("com.chenjw.knife.server.Main");
	    
	    System.out.println(clazz.getClassLoader());
	    clazz.getDeclaredMethod("main", String[].class).invoke(null,new Object[]{(String[])null} );
	}
}