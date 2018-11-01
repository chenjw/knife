package com.chenjw.knife.core.model.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class SystemEnvInfo implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = -8425822015389091072L;
  private Map<String, String> systemEnv;
  private Properties systemProperties;
  private List<Kv<String, String>> vmInfo;

  public Map<String, String> getSystemEnv() {
    return systemEnv;
  }

  public void setSystemEnv(Map<String, String> systemEnv) {
    this.systemEnv = systemEnv;
  }

  public Properties getSystemProperties() {
    return systemProperties;
  }

  public void setSystemProperties(Properties systemProperties) {
    this.systemProperties = systemProperties;
  }

  public List<Kv<String, String>> getVmInfo() {
    return vmInfo;
  }

  public void setVmInfo(List<Kv<String, String>> vmInfo) {
    this.vmInfo = vmInfo;
  }


}
