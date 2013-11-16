package com.chenjw.knife.agent.service.profilertemplate;

import sun.awt.image.ImageWatched.Link;

import com.chenjw.knife.agent.Profiler;

public class LinkAdapter extends Link {

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Object[])) {
            return super.equals(obj);
        }
        Object[] args = (Object[]) obj;
        String name = (String) args[0];
        if (Profiler.METHOD_NAME_ENTER.equals(name)) {
            Profiler.enter(args[1], (String) args[2], (String) args[3], (Object[]) args[4]);
        } else if (Profiler.METHOD_NAME_EXCEPTION_END.equals(name)) {
            Profiler.exceptionEnd(args[1], (String) args[2], (String) args[3], (Object[]) args[4],
                (Throwable) args[5]);
        } else if (Profiler.METHOD_NAME_LEAVE.equals(name)) {
            Profiler
                .leave(args[1], (String) args[2], (String) args[3], (Object[]) args[4], args[5]);
        } else if (Profiler.METHOD_NAME_PROFILE_METHOD.equals(name)) {
            Profiler.profileMethod(args[1], (String) args[2], (String) args[3]);
        } else if (Profiler.METHOD_NAME_PROFILE_STATIC_METHOD.equals(name)) {
            Profiler.profileStaticMethod((Class<?>) args[1], (String) args[2], (String) args[3]);
        } else if (Profiler.METHOD_NAME_RETURN_END.equals(name)) {
            Profiler.returnEnd(args[1], (String) args[2], (String) args[3], (Object[]) args[4],
                args[5]);
        } else if (Profiler.METHOD_NAME_START.equals(name)) {
            Profiler.start(args[1], (String) args[2], (String) args[3], (Object[]) args[4],
                (String) args[5], (String) args[6]);
        }
        return false;
    }

}
