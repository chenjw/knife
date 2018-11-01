package com.chenjw.knife.bytecode.javassist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import com.chenjw.knife.utils.ClassHelper;

/**
 * 工具类
 * 
 * @author chenjw 2012-6-14 上午12:19:48
 */
public class JavassistHelper {

  public static CtClass getComponentType(CtClass ctClass) {
    while (ctClass.isArray()) {
      try {
        ctClass = ctClass.getComponentType();
      } catch (NotFoundException e) {
        e.printStackTrace();
        return ctClass;
      }
    }
    return ctClass;
  }

  public static Class<?> findClass(CtClass ctClass) {
    Class<?> clazz = ClassHelper.findClass(ctClass.getName());
    return clazz;
  }

  public static Method findMethod(CtMethod ctMethod) {
    try {
      CtClass ctClass = ctMethod.getDeclaringClass();
      Class<?> clazz = JavassistHelper.findClass(ctClass);
      String methodName = ctMethod.getName();
      Class<?>[] pClass = new Class<?>[ctMethod.getParameterTypes().length];
      CtClass[] pCtClass = ctMethod.getParameterTypes();
      for (int i = 0; i < pCtClass.length; i++) {
        pClass[i] = JavassistHelper.findClass(pCtClass[i]);
      }
      return clazz.getMethod(methodName, pClass);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  public static CtMethod findCtMethod(CtClass ctClass, Method method) {
    try {
      for (CtMethod ctMethod : ctClass.getMethods()) {
        if (!ctMethod.getName().equals(method.getName())) {
          continue;
        }
        CtClass[] ctParamTypes = ctMethod.getParameterTypes();
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != ctParamTypes.length) {
          continue;
        }
        boolean isEqual = true;
        for (int i = 0; i < paramTypes.length; i++) {
          if (!paramTypes[i].getName()
              .equals(JavassistHelper.findClass(ctParamTypes[i]).getName())) {
            isEqual = false;
          }
        }
        if (isEqual) {
          return ctMethod;
        }
      }
      return null;

    } catch (Exception e) {
      throw new RuntimeException(ctClass.getName() + " " + method.getName(), e);
    }
  }

  /**
   * 生成表示某个字符串常量的表达式
   * 
   * @param str
   * @return
   */
  public static Expression createStringExpression(String str) {
    return new Expression("\"" + str + "\"", String.class);
  }

  public static void main(String[] args) throws NotFoundException, ClassNotFoundException {
    // CtMethod ctMethod = ClassPool.getDefault().getMethod(
    // InvokeLog.class.getName(), "logInvoke1");
    // System.out.println(Helper.findClass(ctMethod.getReturnType()));
    System.out.println(ClassHelper.findClass("[int"));

    System.out.println(int[][].class.getName());
    System.out.println(Integer[][].class.getName());
    System.out.println(ClassHelper.findClass(int[][].class.getName()));
    System.out.println(ClassPool.getDefault().get("void").getName());
    System.out.println(ClassPool.getDefault().get("java.lang.Void").getName());
    System.out.println(Void.class.isAssignableFrom(Object.class));
    System.out.println(new byte[0] instanceof Object);
  }
}
