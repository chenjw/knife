package com.chenjw.knife.agent.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.core.model.result.HeapClassInfo;
import com.chenjw.knife.core.model.result.HeapHistogram;
import com.chenjw.knife.core.model.result.LongValue;
import com.chenjw.knife.utils.IOHelper;


/**
 * 用来检测当前java虚拟机对于jvmti接口的实现情况
 * 
 * @author chenjw
 *
 */
public class HeapHistogramService implements Lifecycle {
  private static final String BOOLEAN_TEXT = "boolean"; // NOI18N
  private static final String CHAR_TEXT = "char"; // NOI18N
  private static final String BYTE_TEXT = "byte"; // NOI18N
  private static final String SHORT_TEXT = "short"; // NOI18N
  private static final String INT_TEXT = "int"; // NOI18N
  private static final String LONG_TEXT = "long"; // NOI18N
  private static final String FLOAT_TEXT = "float"; // NOI18N
  private static final String DOUBLE_TEXT = "double"; // NOI18N
  private static final String VOID_TEXT = "void"; // NOI18N
  private static final char BOOLEAN_CODE = 'Z'; // NOI18N
  private static final char CHAR_CODE = 'C'; // NOI18N
  private static final char BYTE_CODE = 'B'; // NOI18N
  private static final char SHORT_CODE = 'S'; // NOI18N
  private static final char INT_CODE = 'I'; // NOI18N
  private static final char LONG_CODE = 'J'; // NOI18N
  private static final char FLOAT_CODE = 'F'; // NOI18N
  private static final char DOUBLE_CODE = 'D'; // NOI18N
  private static final char OBJECT_CODE = 'L'; // NOI18N



  private static final String DIAGNOSTIC_COMMAND_MXBEAN_NAME =
      "com.sun.management:type=DiagnosticCommand"; // NOI18N
  private static final String ALL_OBJECTS_OPTION = "-all"; // NOI18N
  private static final String HISTOGRAM_COMMAND = "gcClassHistogram"; // NOI18N
  private MBeanServer mserver;
  private ObjectName hotspotDiag;

  @Override
  public void init() {
    try {
      mserver = ManagementFactory.getPlatformMBeanServer();
      hotspotDiag = new ObjectName(DIAGNOSTIC_COMMAND_MXBEAN_NAME);
      mserver.getObjectInstance(hotspotDiag);
    } catch (MalformedObjectNameException ex) {
      ex.printStackTrace();
    } catch (InstanceNotFoundException ex) {
      System.err.println("Heap Histogram is not available"); // NOI18N
    } catch (SecurityException ex) {
      ex.printStackTrace();
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }

  }

  public HeapHistogram getHeapHistogram() throws IOException {
    InputStream is = this.getRawHistogram();
    try {
      return parse(IOHelper.toString(is, "UTF-8"));
    } finally {
      IOHelper.closeQuietly(is);
    }
  }

  private HeapHistogram parse(String histogramText) {
    //System.out.println(histogramText);
    HeapHistogram heapHistogram = new HeapHistogram();
    SortedMap<String, HeapClassInfo> classesMap = new TreeMap<String, HeapClassInfo>();

    heapHistogram.setTime(new Date());
    Scanner sc = new Scanner(histogramText);
    try {
      sc.useRadix(10);
      while (!sc.hasNext("-+")) {
        sc.nextLine();
      }
      sc.skip("-+");
      sc.nextLine();

      while (sc.hasNext("[0-9]+:")) { // NOI18N
        HeapClassInfo newClInfo = new HeapClassInfo();
        String jvmName;
        sc.next();
        newClInfo.setInstancesCount(new LongValue(sc.nextLong()));
        newClInfo.setBytes(new LongValue(sc.nextLong()));
        jvmName = sc.next();
        sc.nextLine(); // skip module name on JDK 9
        newClInfo.setName(convertJVMName(jvmName));
        storeClassInfo(newClInfo, classesMap);

      }
      sc.next("Total"); // NOI18N
      heapHistogram.setTotalInstances(new LongValue(sc.nextLong()));
      heapHistogram.setTotalBytes(new LongValue(sc.nextLong()));
      heapHistogram.setClasses(new ArrayList<HeapClassInfo>(classesMap.values()));
      return heapHistogram;
    } finally {
      sc.close();
    }

  }

  private void storeClassInfo(final HeapClassInfo newClInfo, final Map<String, HeapClassInfo> map) {
    HeapClassInfo oldClInfo = map.get(newClInfo.getName());
    if (oldClInfo == null) {
      map.put(newClInfo.getName(), newClInfo);
    } else {
      oldClInfo.setBytes(
          new LongValue(oldClInfo.getBytes().getValue() + newClInfo.getBytes().getValue()));
      oldClInfo.setInstancesCount(new LongValue(
          oldClInfo.getInstancesCount().getValue() + newClInfo.getInstancesCount().getValue()));
    }
  }

  private InputStream getRawHistogram() {
    try {
      Object histo = mserver.invoke(hotspotDiag, HISTOGRAM_COMMAND,
          new Object[] {new String[] {ALL_OBJECTS_OPTION}},
          new String[] {String[].class.getName()});
      if (histo instanceof String) {
        return new ByteArrayInputStream(((String) histo).getBytes("UTF-8"));
      }
    } catch (InstanceNotFoundException ex) {
      ex.printStackTrace();
    } catch (MBeanException ex) {
      ex.printStackTrace();
    } catch (ReflectionException ex) {
      ex.printStackTrace();
    } catch (UnsupportedEncodingException e) {

      e.printStackTrace();
    }
    return null;
  }

  private String convertJVMName(String jvmName) {
    String className = null;
    int index = jvmName.lastIndexOf('['); // NOI18N

    if (index != -1) {
      switch (jvmName.charAt(index + 1)) {
        case BOOLEAN_CODE:
          className = BOOLEAN_TEXT;
          break;
        case CHAR_CODE:
          className = CHAR_TEXT;
          break;
        case BYTE_CODE:
          className = BYTE_TEXT;
          break;
        case SHORT_CODE:
          className = SHORT_TEXT;
          break;
        case INT_CODE:
          className = INT_TEXT;
          break;
        case LONG_CODE:
          className = LONG_TEXT;
          break;
        case FLOAT_CODE:
          className = FLOAT_TEXT;
          break;
        case DOUBLE_CODE:
          className = DOUBLE_TEXT;
          break;
        case OBJECT_CODE:
          className = jvmName.substring(index + 2, jvmName.length() - 1);
          break;
        default:
          System.err.println("Uknown name " + jvmName); // NOI18N
          className = jvmName;
      }
      for (int i = 0; i <= index; i++) {
        className += "[]";
      }
    }
    if (className == null) {
      className = jvmName;
    }
    return className.intern();
  }

  @Override
  public void clear() {

  }

  @Override
  public void close() {
    mserver = null;
    hotspotDiag = null;
  }
}
