package com.chenjw.knife.agent.service.profilertemplate;

import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.Profiler;
import com.chenjw.knife.agent.core.ProfilerTemplate;
import com.chenjw.knife.utils.StringHelper;

public class DefaultProfilerTemplate implements ProfilerTemplate {

  private static final Class<Profiler> PROFILER_CLASS = Profiler.class;

  @Override
  public void init() {}

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

  public String newObjectCode(String[] args) {
    return getCode(Profiler.METHOD_NAME_NEW_OBJECT, args);
  }
  @Override
  public String newArrayCode(String[] args) {
    return getCode(Profiler.METHOD_NAME_NEW_ARRAY, args);
  }



  public String voidCode() {
    return "\"" + Profiler.VOID + "\"";
  }

  private String getCode(String methodName, String[] args) {
    StringBuffer sb = new StringBuffer();
    sb.append(PROFILER_CLASS.getName() + "." + methodName);
    sb.append("(");
    sb.append(StringHelper.join(args, ","));
    sb.append(");");
    return sb.toString();
  }

  public int getStackTraceStartIndex(StackTraceElement[] stes) {
    for (int i = 0; i < stes.length; i++) {
      StackTraceElement ste = stes[i];
      String cn = ste.getClassName();
      String mn = ste.getMethodName();
      if (cn == null) {
        continue;
      }
      if ("sendEvent".equals(mn) && cn.equals(Profiler.class.getName())) {
        return i + 1;
      }
    }
    throw new IllegalStateException("stackTrace errors : " + JSON.toJSONString(stes, true));
  }


}
