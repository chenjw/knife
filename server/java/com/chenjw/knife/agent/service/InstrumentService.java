package com.chenjw.knife.agent.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

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

/**
 * 用于增强某个类的某个方法，增强后的方法会在一些地方发出事件（Event）
 * 
 * @author chenjw
 *
 */
public class InstrumentService implements Lifecycle {

    private static final String[] CLASS_WHITE_LIST    = new String[] {
            "java.lang.reflect.InvocationHandler.invoke", "java.lang.reflect.Method.invoke" };

    private final Set<String>     TRACED_METHOD       = new HashSet<String>();

    private final Set<String>     ENTER_TRACED_METHOD = new HashSet<String>();

    private ProfilerTemplate      template            = new StubProfilerTemplate();

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
            return;
        }

        ClassGenerator newClassGenerator = ClassGenerator.newInstance(method.getDeclaringClass()
            .getName(), new ClassLoaderClassPath(AgentClassLoader.getAgentClassLoader()));
        CtClass ctClass = newClassGenerator.getCtClass();
        CtMethod newMethod = JavassistHelper.findCtMethod(ctClass, method);

        if (newMethod != null) {
            newMethod.instrument(new MethodCallExprEditor());
            // add enter leave code
            // addEnterLeaveCode(ctClass, newMethod);
            byte[] classBytes = newClassGenerator.toBytecode();

            ServiceRegistry.getService(ByteCodeService.class).tryRedefineClass(
                method.getDeclaringClass(), classBytes);
            ServiceRegistry.getService(ByteCodeService.class).commitAll();

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
            return;
        }
        ClassGenerator newClassGenerator = ClassGenerator.newInstance(method.getDeclaringClass()
            .getName(), new ClassLoaderClassPath(AgentClassLoader.getAgentClassLoader()));
        CtClass ctClass = newClassGenerator.getCtClass();
        CtMethod newMethod = JavassistHelper.findCtMethod(ctClass, method);
        if (newMethod != null) {
            // add enter leave code
            addEnterLeaveCode(ctClass, newMethod);
            byte[] classBytes = newClassGenerator.toBytecode();
            ServiceRegistry.getService(ByteCodeService.class).tryRedefineClass(
                JavassistHelper.findClass(newClassGenerator.getCtClass()), classBytes);
            ServiceRegistry.getService(ByteCodeService.class).commitAll();
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
                String beforeCode = template.enterCode(new String[] { "null",// target
                        wrrapString(className),//类名
                        wrrapString(methodName),//方法名
                        "$args"//参数
                });
                String afterCode = template.leaveCode(new String[] { "null", //target
                        wrrapString(className),//类名
                        wrrapString(methodName),//方法名
                        "$args",//参数
                        resultExpr // 结果
                    });
                ctMethod.insertBefore("{" + beforeCode + "}");
                ctMethod.insertAfter("{" + afterCode + "}", true);
            } else {
                String beforeCode = template.enterCode(new String[] { "$0", wrrapString(className),
                        wrrapString(methodName), "$args" });
                String afterCode = template.leaveCode(new String[] { "$0", wrrapString(className),
                        wrrapString(methodName), "$args", resultExpr });
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
                return;
            }
            Class<?> returnClass = null;
            try {
                returnClass = JavassistHelper.findClass(ctMethod.getReturnType());
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
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

                proxyCode = template.profileStaticCode(new String[] { "$class",
                        wrrapString(className), wrrapString(methodName)

                });
            } else if ("java.lang.reflect.Method".equals(className) && "invoke".equals(methodName)) {

                proxyCode = template.profileCode(new String[] { "$1",
                        "$0.getDeclaringClass().getName()", "$0.getName()" });
            } else {

                proxyCode = template.profileCode(new String[] { "$0", wrrapString(className),
                        wrrapString(methodName) });
            }

            String startCode = template.startCode(new String[] { "$0", wrrapString(className),
                    wrrapString(methodName), "$args", wrrapString(methodcall.getFileName()),
                   "\""+ String.valueOf(methodcall.getLineNumber())+"\"" });

            String returnEndCode = template.returnEndCode(new String[] { "$0",
                    wrrapString(className), wrrapString(methodName), "$args", resultExpr });

            String exceptionEndCode = template.exceptionEndCode(new String[] { "$0",
                    wrrapString(className), wrrapString(methodName), "$args", "$e" });
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

    private String wrrapString(String str) {
        return "\"" + str + "\"";
    }

    @Override
    public void init() {

    }

    @Override
    public void clear() {
        TRACED_METHOD.clear();
        ENTER_TRACED_METHOD.clear();
    }

    @Override
    public void close() {

    }
}
