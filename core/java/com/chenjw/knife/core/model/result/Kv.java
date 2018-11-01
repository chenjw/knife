package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class Kv<K, V> implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -1211502788419603261L;
  private K key;
  private V value;

  public Kv() {

  }

  public Kv(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }

}
