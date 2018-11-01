package com.chenjw.knife.agent.service;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.bytecode.javassist.ClassLoaderClassPath;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.agent.core.ProfilerTemplate;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.profilertemplate.DefaultProfilerTemplate;
import com.chenjw.knife.agent.service.profilertemplate.StubProfilerTemplate;
import com.chenjw.knife.bytecode.javassist.ClassGenerator;
import com.chenjw.knife.bytecode.javassist.JavassistHelper;
import com.chenjw.knife.utils.ClassHelper;
import com.chenjw.knife.utils.FileHelper;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;

/**
 * 用于增强某个类的某个方法，增强后的方法会在一些地方发出事件（Event）
 * 
 * @author chenjw
 *
 */
public class InstrumentService implements Lifecycle {

  private static final String[] CLASS_WHITE_LIST = new String[] {
      "java.lang.reflect.InvocationHandler.invoke", "java.lang.reflect.Method.invoke"};

  private final Set<String> TRACED_METHOD = new HashSet<String>();

  private final Set<String> ENTER_TRACED_METHOD = new HashSet<String>();

  private final Set<String> ENTER_TRACED_CONSTRUCTOR = new HashSet<String>();

  // public static ProfilerTemplate template = new StubProfilerTemplate();
  public static ProfilerTemplate template = new DefaultProfilerTemplate();

  /**
   * @param instrumentClazz 需要做字节码增强的class
   * @param needEventClazzes 需要监听new的class
   */
  public void buildNewExpr(Class instrumentClazz, List<Class> needEventClazzes) {
    if (!isCanTrace(instrumentClazz)) {
      return;
    }
    // filter unsupport method
    template.init();
    // System.err.println("instrument=" + instrumentClazz.getName());
    if (instrumentClazz.getClassLoader() == null) {
      return;
    }

    ClassGenerator newClassGenerator = ClassGenerator.newInstance(instrumentClazz.getName(),
        new ClassLoaderClassPath(instrumentClazz.getClassLoader()));
    CtClass ctClass = newClassGenerator.getCtClass();
    if (ctClass == null) {
      return;
    }
    try {

      NewObjectOrArrayCallExprEditor editor = new NewObjectOrArrayCallExprEditor(needEventClazzes);
      ctClass.instrument(editor);
      if (editor.instrumented) {
        System.err.println("instrumented " + instrumentClazz.getName());
        byte[] classBytes = newClassGenerator.toBytecode();
        try {
          FileHelper.writeByteArrayToFile(new File("/Users/chenjw/workspace/knife/insturment/"
              + instrumentClazz.getSimpleName() + ".class"), classBytes);

          ServiceRegistry.getService(ByteCodeService.class).tryRedefineClass(instrumentClazz,
              classBytes);
          ServiceRegistry.getService(ByteCodeService.class).commitAll();
        } catch (Exception e) {
          if (Agent.isDebugOn()) {
            Agent.debug("redefine class fail " + instrumentClazz);
          }
          e.printStackTrace();
        }
      } else {
        System.err.println("not instrumented " + instrumentClazz.getName());
      }


    } catch (Exception e) {
      System.err.println("instrument fail " + instrumentClazz.getName());

      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private void buildMethodAccess(Method method) throws Exception {

    // System.out.println(InstrumentManager.class.getClassLoader());
    String methodFullName = method.toGenericString();
    // filter traced method
    if (TRACED_METHOD.contains(methodFullName)) {
      return;
    } else {
      TRACED_METHOD.add(methodFullName);
    }
    // filter unsupport method
    if (!isSupportTrace(method.getDeclaringClass().getName(), method.getName(),
        method.getModifiers())) {
      if (Agent.isDebugOn()) {
        Agent.debug(
            "not support trace when buildMethodAccess : " + method.getDeclaringClass().getName()
                + "." + method.getName() + " " + method.getModifiers());
      }
      return;
    }

    ClassGenerator newClassGenerator =
        ClassGenerator.newInstance(method.getDeclaringClass().getName(),
            new ClassLoaderClassPath(AgentClassLoader.getAgentClassLoader()));
    CtClass ctClass = newClassGenerator.getCtClass();

    CtMethod newMethod = JavassistHelper.findCtMethod(ctClass, method);

    if (newMethod != null) {
      newMethod.instrument(new MethodCallExprEditor());
      // add enter leave code
      // addEnterLeaveCode(ctClass, newMethod);
      byte[] classBytes = newClassGenerator.toBytecode();
      try {
        ServiceRegistry.getService(ByteCodeService.class)
            .tryRedefineClass(method.getDeclaringClass(), classBytes);
        ServiceRegistry.getService(ByteCodeService.class).commitAll();
      } catch (Exception e) {
        if (Agent.isDebugOn()) {
          Agent.debug("redefine class fail " + method.getDeclaringClass());
        }
        e.printStackTrace();
      }
    }

  }

  public void buildConstructorEnterLeave(Class clazz) throws Exception {
    // filter traced method
    if (ENTER_TRACED_CONSTRUCTOR.contains(clazz.getName())) {
      return;
    } else {
      ENTER_TRACED_CONSTRUCTOR.add(clazz.getName());
    }
    // filter unsupport method
    template.init();

    // if (!isSupportTrace(method.getDeclaringClass().getName(), method.getName(),
    // method.getModifiers())) {
    // if(Agent.isDebugOn()){
    // Agent.debug("not support trace when buildMethodEnterLeave :
    // "+method.getDeclaringClass().getName()+"."+method.getName()+" "+method.getModifiers());
    // }
    // return;
    // }
    ClassGenerator newClassGenerator = ClassGenerator.newInstance(clazz.getName(),
        new ClassLoaderClassPath(AgentClassLoader.getAgentClassLoader()));
    CtClass ctClass = newClassGenerator.getCtClass();
    for (CtConstructor newConstructor : ctClass.getConstructors()) {

      // add enter leave code
      addEnterLeaveCode(ctClass, newConstructor);
    }
    byte[] classBytes = newClassGenerator.toBytecode();
    FileHelper.writeByteArrayToFile(new File("/Users/chenjw/workspace/knife/TestClass.class"),
        classBytes);
    try {
      ServiceRegistry.getService(ByteCodeService.class)
          .tryRedefineClass(JavassistHelper.findClass(newClassGenerator.getCtClass()), classBytes);
      ServiceRegistry.getService(ByteCodeService.class).commitAll();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void buildMethodEnterLeave(Method method) throws Exception {
    String methodFullName = method.toGenericString();
    // filter traced method
    if (ENTER_TRACED_METHOD.contains(methodFullName)) {
      return;
    } else {
      ENTER_TRACED_METHOD.add(methodFullName);
    }
    // filter unsupport method
    template.init();

    if (!isSupportTrace(method.getDeclaringClass().getName(), method.getName(),
        method.getModifiers())) {
      if (Agent.isDebugOn()) {
        Agent.debug(
            "not support trace when buildMethodEnterLeave : " + method.getDeclaringClass().getName()
                + "." + method.getName() + " " + method.getModifiers());
      }
      return;
    }
    ClassGenerator newClassGenerator =
        ClassGenerator.newInstance(method.getDeclaringClass().getName(),
            new ClassLoaderClassPath(AgentClassLoader.getAgentClassLoader()));
    CtClass ctClass = newClassGenerator.getCtClass();
    CtMethod newMethod = JavassistHelper.findCtMethod(ctClass, method);
    if (newMethod != null) {
      // add enter leave code
      addEnterLeaveCode(ctClass, newMethod);
      byte[] classBytes = newClassGenerator.toBytecode();
      try {
        ServiceRegistry.getService(ByteCodeService.class).tryRedefineClass(
            JavassistHelper.findClass(newClassGenerator.getCtClass()), classBytes);
        ServiceRegistry.getService(ByteCodeService.class).commitAll();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  private void addEnterLeaveCode(CtClass ctClass, CtConstructor ctConstructor) {
    try {

      String constructorName = ctConstructor.getLongName();
      //
      String className = ClassHelper.makeClassName(JavassistHelper.findClass(ctClass));
      String beforeCode = template.newObjectCode(
          new String[] {"$0", wrapString(className), wrapString(constructorName), "$args"});

      System.out.println(beforeCode);



      // ctConstructor.insertBeforeBody(src);

      ctConstructor.insertBeforeBody("{" + beforeCode + "}");



    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void addEnterLeaveCode(CtClass ctClass, CtMethod ctMethod) {
    try {
      // ////////////////
      Class<?> returnClass = null;
      try {
        returnClass = JavassistHelper.findClass(ctMethod.getReturnType());
      } catch (NotFoundException e) {
        e.printStackTrace();
      }

      String resultExpr = null;
      if (returnClass == void.class) {
        resultExpr = template.voidCode();
      } else {
        resultExpr = "($w)$_";
      }
      //
      String className = ClassHelper.makeClassName(JavassistHelper.findClass(ctClass));
      String methodName = ctMethod.getName();
      // // /////////
      if (Modifier.isStatic(ctMethod.getModifiers())) {
        String beforeCode = template.enterCode(new String[] {"null", // target
            wrapString(className), // 类名
            wrapString(methodName), // 方法名
            "$args"// 参数
        });
        String afterCode = template.leaveCode(new String[] {"null", // target
            wrapString(className), // 类名
            wrapString(methodName), // 方法名
            "$args", // 参数
            resultExpr // 结果
        });
        ctMethod.insertBefore("{" + beforeCode + "}");
        ctMethod.insertAfter("{" + afterCode + "}", true);
      } else {
        String beforeCode = template
            .enterCode(new String[] {"$0", wrapString(className), wrapString(methodName), "$args"});
        String afterCode = template.leaveCode(new String[] {"$0", wrapString(className),
            wrapString(methodName), "$args", resultExpr});
        ctMethod.insertBefore("{" + beforeCode + "}");
        ctMethod.insertAfter("{" + afterCode + "}", true);

      }
    } catch (CannotCompileException e) {
      e.printStackTrace();
    }
  }

  private boolean isCanTrace(Class<?> clazz) {
    if (clazz.isArray()) {
      return false;
    } else if (clazz.isInterface()) {
      return false;
    } else {
      return true;
    }
  }

  public void buildTraceMethod(Method method) throws Exception {
    if (!isCanTrace(method.getDeclaringClass())) {
      if (Agent.isDebugOn()) {
        Agent.debug("not trace " + method.getDeclaringClass().getName());
      }
      return;
    }
    template.init();
    buildMethodAccess(method);
  }

  private static boolean isSupportClassNameAndMethodName(String className, String methodName) {
    // filter name
    if (className.equals(Profiler.class.getName())) {
      return false;
    }
    boolean isLog = true;
    if (className.startsWith("java.")) {
      isLog = false;
    } else if (className.startsWith("javax.")) {
      isLog = false;
    } else if (className.startsWith("sun.")) {
      isLog = false;
    } else if (className.startsWith("com.sun.")) {
      isLog = false;
    }
    String name = className + "." + methodName;
    // pass for white list
    if (!isLog) {
      for (String cn : CLASS_WHITE_LIST) {
        if (name.equals(cn)) {
          isLog = true;
          break;
        }
      }
    }
    return isLog;
  }

  private static boolean isSupportTrace(String className, String methodName, int methodModifier) {
    // filter name
    if (!isSupportClassNameAndMethodName(className, methodName)) {
      return false;
    }
    // filter native
    if (Modifier.isNative(methodModifier)) {
      return false;
    } else {
      return true;
    }
  }

  private class MethodCallExprEditor extends ExprEditor {

    public void edit(MethodCall methodcall) throws CannotCompileException {

      String className = methodcall.getClassName();
      String methodName = methodcall.getMethodName();
      CtMethod ctMethod = null;
      try {
        ctMethod = methodcall.getMethod();
      } catch (NotFoundException e1) {
        throw new RuntimeException(methodName + " not found!", e1);
      }
      if (!isSupportTrace(className, ctMethod.getName(), ctMethod.getModifiers())) {
        if (Agent.isDebugOn()) {
          Agent.debug("not support trace when edit method : " + className + "." + ctMethod.getName()
              + " " + ctMethod.getModifiers());
        }
        return;
      }
      Class<?> returnClass = null;
      try {
        returnClass = JavassistHelper.findClass(ctMethod.getReturnType());
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
      String resultExpr = null;
      if (returnClass == void.class) {
        resultExpr = template.voidCode();
      } else {
        resultExpr = "($w)$_";
      }
      String proxyCode = null;

      if (isStatic(ctMethod)) {

        proxyCode = template.profileStaticCode(
            new String[] {"java.lang.Class.forName(" + wrapString(className) + ")",
                wrapString(className), wrapString(methodName)

            });
      } else if ("java.lang.reflect.Method".equals(className) && "invoke".equals(methodName)) {

        proxyCode = template
            .profileCode(new String[] {"$1", "$0.getDeclaringClass().getName()", "$0.getName()"});
      } else {

        proxyCode = template
            .profileCode(new String[] {"$0", wrapString(className), wrapString(methodName)});
      }

      String startCode = template.startCode(new String[] {"$0", wrapString(className),
          wrapString(methodName), "$args", wrapString(methodcall.getFileName()),
          wrapString(String.valueOf(methodcall.getLineNumber()))});

      String returnEndCode = template.returnEndCode(
          new String[] {"$0", wrapString(className), wrapString(methodName), "$args", resultExpr});

      String exceptionEndCode = template.exceptionEndCode(
          new String[] {"$0", wrapString(className), wrapString(methodName), "$args", "$e"});
      StringBuffer code = new StringBuffer("try{");
      code.append(startCode);
      code.append(proxyCode);
      code.append("$_=$proceed($$);");
      code.append(returnEndCode);
      code.append("}");
      code.append("catch(java.lang.Throwable $e){");
      code.append(exceptionEndCode);
      code.append("throw $e;");
      code.append("}");
      methodcall.replace(code.toString());
    }

    private boolean isStatic(CtMethod ctMethod) {
      return Modifier.isStatic(ctMethod.getModifiers());
    }

  }



  private class NewObjectOrArrayCallExprEditor extends ExprEditor {
    private List<Class> needEventClazzes;
    private boolean instrumented = false;

    public NewObjectOrArrayCallExprEditor(List<Class> needEventClazzes) {
      this.needEventClazzes = needEventClazzes;
    }

    private boolean needEvent(String className) {
      if (needEventClazzes == null || className == null) {
        return false;
      }
      for (Class needEventClazz : needEventClazzes) {
        // System.err.println(needEventClazzes);
        if (className.equals(needEventClazz.getName())) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void edit(NewExpr newExpr) throws CannotCompileException {
      try {

        // System.out.println("-------start");
        //
        // System.out.println("ClassName=" + newExpr.getClassName());
        // System.out.println("FileName=" + newExpr.getFileName());
        // System.out.println("Signature=" + newExpr.getSignature());
        //
        // System.out
        // .println("Constructor.Signature=" + newExpr.getConstructor().getGenericSignature());
        // System.out.println("Constructor.ConstructorName=" + newExpr.getConstructor().getName());
        // System.out.println("Constructor.LongName=" + newExpr.getConstructor().getLongName());
        //
        //
        //
        // System.out.println("-------end");
        String text = "";
        // text += "System.out.println(123);";
        text += "$_=$proceed($$);";
        text += "System.out.print(\"result= \");";
        text += "System.out.println($_);";
        text += "System.out.print(\"type= \");";
        text += "System.out.println($type);";
        // text += "System.out.println(\"created \"+$_);";

        // newExpr.replace(text);



        CtConstructor ctConstructor = newExpr.getConstructor();
        String className = newExpr.getClassName();
        if (!needEvent(className)) {
          super.edit(newExpr);
        } else {
          String constructorName = ctConstructor.getLongName();
          //
          StringBuffer sb = new StringBuffer();
          String code = template.newObjectCode(
              new String[] {"$_", wrapString(className), wrapString(constructorName), "$args"});
          sb.append("$_=$proceed($$);");
          sb.append(code);
          System.out.println(sb.toString());

          newExpr.replace(sb.toString());
          instrumented = true;
        }
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void edit(NewArray newExpr) throws CannotCompileException {
      try {
        
        CtClass componentType = newExpr.getComponentType();

        if (!needEvent(componentType.getName())) {
          super.edit(newExpr);
        } else {
       
          //
          StringBuffer sb = new StringBuffer();
          String code = template.newObjectCode(
              new String[] {"$_", wrapString(componentType.getName())});
          sb.append("$_=$proceed($$);");
          sb.append(code);
          System.out.println(sb.toString());

          newExpr.replace(sb.toString());
          instrumented = true;


        }
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }



  }



  private String wrapString(String str) {
    return "\"" + str + "\"";
  }

  @Override
  public void init() {

  }

  @Override
  public void clear() {
    TRACED_METHOD.clear();
    ENTER_TRACED_METHOD.clear();
    ENTER_TRACED_CONSTRUCTOR.clear();
  }

  @Override
  public void close() {

  }
}
