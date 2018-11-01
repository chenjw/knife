/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved. DO NOT ALTER OR
 * REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License version 2 only, as published by the Free Software Foundation. Oracle
 * designates this particular file as subject to the "Classpath" exception as provided by Oracle in
 * the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version 2 along with this work;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA or visit www.oracle.com
 * if you need additional information or have any questions.
 */

package com.chenjw.knife.core.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tomas Hurka
 */
public class HeapHistogram implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2077188341355027912L;
  private List<HeapClassInfo> classes = new ArrayList<HeapClassInfo>();

  /**
   * 计算过程中临时存放
   */
  private Map<String, HeapClassInfo> tempClassesMap;

  private Date time;
  private LongValue totalBytes = new LongValue();

  private LongValue totalInstances = new LongValue();


  public List<HeapClassInfo> getClasses() {
    return classes;
  }

  public void setClasses(List<HeapClassInfo> classes) {
    this.classes = classes;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }



  public Map<String, HeapClassInfo> getTempClassesMap() {
    return tempClassesMap;
  }

  public void setTempClassesMap(Map<String, HeapClassInfo> tempClassesMap) {
    this.tempClassesMap = tempClassesMap;
  }

  public LongValue getTotalBytes() {
    return totalBytes;
  }

  public void setTotalBytes(LongValue totalBytes) {
    this.totalBytes = totalBytes;
  }

  public LongValue getTotalInstances() {
    return totalInstances;
  }

  public void setTotalInstances(LongValue totalInstances) {
    this.totalInstances = totalInstances;
  }



}
