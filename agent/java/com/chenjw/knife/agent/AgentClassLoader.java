package com.chenjw.knife.agent;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class AgentClassLoader extends URLClassLoader {

  private static AgentClassLoader instance = null;
  private ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
  private ClassLoader parent;

  public AgentClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, null);
    this.parent = parent;
  }

  public boolean isLoadByAgent(Class<?> clazz) {
    Class<?> class1 = this.findLoadedClass(clazz.getName());
    return class1 == clazz;
  }


  @Override
  protected synchronized Class<?> loadClass(String s, boolean flag) throws ClassNotFoundException {
   
    Class<?> class1=null;
    try {
       class1 = this.findLoadedClass(s);
      if (class1 == null) {
        try {
          class1 = findClass(s);
        } catch (ClassNotFoundException e) {
          //e.printStackTrace();
          // System.out.println("parent " + parent.getClass()
          // + " cant find class " + s);
        }
      }
      if (class1 == null) {
        if (parent != null) {
          try {
            class1 = parent.loadClass(s);
          } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            class1 = systemClassLoader.loadClass(s);

          }
        }
      }
      if (flag)
        resolveClass(class1);
      return class1;
    }
    finally {
      //System.out.println("AgentClassLoader.loadClass "+s+" "+class1);
    }
   
  }

  public static AgentClassLoader getAgentClassLoader() {
    return instance;
  }

  public static void setAgentClassLoader(AgentClassLoader agentClassLoader) {
    instance = agentClassLoader;
  }

  public void setParent(ClassLoader parent) {
    if (parent == null) {
      this.parent = ClassLoader.getSystemClassLoader();
    } else {
      this.parent = parent;
    }

  }

  @Override
  public String toString() {
    return "AgentClassLoader [parent=" + parent + ", getURLs()=" + Arrays.toString(getURLs()) + "]";
  }

}
