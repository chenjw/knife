package com.chenjw.knife.agent.core;


public interface ProfilerTemplate {

    public void init();
    
    public String profileCode(String [] args);
    
    public String profileStaticCode(String [] args);
    
    public String exceptionEndCode(String [] args);

    public String returnEndCode(String [] args);
    
    public String startCode(String [] args);
    
    public String enterCode(String [] args);
    
    public String leaveCode(String [] args);
    
    public String voidCode();

}
