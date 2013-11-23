package com.chenjw.knife.server.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class NewClassLoader extends URLClassLoader  {
    
    public NewClassLoader() throws MalformedURLException{
        super(new URL[]{
                        new URL("file","","C:\\Users\\chenjw\\Desktop\\a.jar"),
                        new URL("file","","C:\\my_workspace\\knife\\lib\\lib\\sourceforge.spring-2.5.6.jar"),
                        new URL("file","","C:\\my_workspace\\knife\\lib\\lib\\commons-logging-1.1.1.jar"),
                        new URL("file","","C:\\my_workspace\\knife\\lib\\lib\\aspectjrt.jar"),
                        new URL("file","","C:\\my_workspace\\knife\\lib\\lib\\aspectjweaver.jar"),
                        new URL("file","","C:\\my_workspace\\knife\\lib\\lib\\cglib-nodep-2.1_3.jar"),
                        new URL("file","","C:\\my_workspace\\knife\\lib\\lib\\java.j2ee-1.4.jar")
        },null);
        
    }
    

    public synchronized Class<?> loadClass(String name)
                                                                           throws ClassNotFoundException {
       // System.out.println(name);
       // if(name.startsWith("javassist.")){
        //    return null;
       // }
        //else{
            return super.loadClass(name);
        //}
    }



}
