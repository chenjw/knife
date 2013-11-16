package com.chenjw.knife.agent.service.profilertemplate;

import java.lang.reflect.Method;

import javassist.CtMethod;
import sun.awt.image.ImageWatched;

import com.chenjw.knife.agent.AgentClassLoader;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.bytecode.javassist.ClassLoaderClassPath;
import com.chenjw.knife.agent.core.ProfilerTemplate;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ByteCodeService;
import com.chenjw.knife.agent.utils.NativeHelper;
import com.chenjw.knife.bytecode.javassist.ClassGenerator;
import com.chenjw.knife.bytecode.javassist.JavassistHelper;
import com.chenjw.knife.bytecode.javassist.method.ReplaceMethodGenerator;
import com.chenjw.knife.utils.StringHelper;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;

public class StubProfilerTemplate implements ProfilerTemplate {
    private static final Class<StubAdapter> CLASS       = StubAdapter.class;
    private static final String             CLASS_NAME  = StubAdapter.class.getName();
    private static final String             METHOD_NAME = "isStub";
    private static Method                   METHOD;

    private static final String             MARK        = "aaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    static {
        try {
            METHOD = StubAdapter.class.getDeclaredMethod(METHOD_NAME, Object.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if (StubAdapter.isStub(MARK)) {
            return;
        }
        ClassGenerator classGenerator = ClassGenerator.newInstance(CLASS_NAME,
            new ClassLoaderClassPath(AgentClassLoader.getAgentClassLoader()));
        CtMethod ctMethod = JavassistHelper.findCtMethod(classGenerator.getCtClass(),
            StubProfilerTemplate.METHOD);
        ReplaceMethodGenerator methodGenerator = ReplaceMethodGenerator.newInstance(ctMethod);

        methodGenerator.addExpression("if(\"" + MARK + "\".equals($1)){return true;}");
        methodGenerator
            .addExpression("if(($1 instanceof com.sun.corba.se.spi.presentation.rmi.DynamicStub)||($1 instanceof org.omg.CORBA.portable.ObjectImpl)){return true;}");
        methodGenerator
            .addExpression("if($1 instanceof Object[]){ sun.awt.image.ImageWatched.endlink.equals((Object[])$1) ;}");
        methodGenerator.addExpression("return false;");
        classGenerator.addMethod(methodGenerator);
        byte[] classBytes = classGenerator.toBytecode();
        ServiceRegistry.getService(ByteCodeService.class).tryRedefineClass(CLASS, classBytes);
        ServiceRegistry.getService(ByteCodeService.class).commitAll();
        NativeHelper.setStaticFieldValue(ImageWatched.class, "endlink", new LinkAdapter());
    }

    public String profileCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_PROFILE_METHOD, args);
    }

    public String profileStaticCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_PROFILE_STATIC_METHOD, args);
    }

    public String exceptionEndCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_EXCEPTION_END, args);
    }

    public String returnEndCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_RETURN_END, args);
    }

    public String startCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_START, args);
    }

    public String enterCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_ENTER, args);
    }

    public String leaveCode(String[] args) {
        return getCode(Profiler.METHOD_NAME_LEAVE, args);
    }

    public String voidCode() {
        return Profiler.class.getName() + ".VOID";
    }

    private String getCode(String methodName, String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append(CLASS_NAME + "." + METHOD_NAME);
        sb.append("(new Object[]{");
        sb.append("\""+methodName + "\",");
        sb.append(StringHelper.join(args, ","));
        sb.append("});");
        return sb.toString();
    }

}
