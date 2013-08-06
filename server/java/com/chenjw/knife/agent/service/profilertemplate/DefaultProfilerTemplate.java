package com.chenjw.knife.agent.service.profilertemplate;

import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.core.ProfilerTemplate;
import com.chenjw.knife.utils.StringHelper;

public class DefaultProfilerTemplate implements ProfilerTemplate {

    private static final Class<Profiler> PROFILER_CLASS = Profiler.class;

    @Override
    public void init() {
    }
    
    public String profileCode(String [] args){
        return getCode(Profiler.METHOD_NAME_PROFILE_METHOD,args);
    }
    
    public String profileStaticCode(String [] args){
        return getCode(Profiler.METHOD_NAME_PROFILE_STATIC_METHOD,args);
    }
    
    public String exceptionEndCode(String [] args){
        return getCode(Profiler.METHOD_NAME_EXCEPTION_END,args);
    }

    public String returnEndCode(String [] args){
        return getCode(Profiler.METHOD_NAME_RETURN_END,args);
    }
    
    public String startCode(String [] args){
        return getCode(Profiler.METHOD_NAME_START,args);
    }
    
    public String enterCode(String [] args){
        return getCode(Profiler.METHOD_NAME_ENTER,args);
    }
    
    public String leaveCode(String [] args){
        return getCode(Profiler.METHOD_NAME_LEAVE,args);
    }
    
    public String voidCode(){
       return PROFILER_CLASS.getName() + ".VOID";
    }
    
    private String getCode(String methodName,String[] args){
        StringBuffer sb=new StringBuffer();
        sb.append( PROFILER_CLASS.getName()        + "."       + methodName);
        sb.append("(");
        sb.append(StringHelper.join(args,","));
        sb.append(");");
        return sb.toString();
    }


}
