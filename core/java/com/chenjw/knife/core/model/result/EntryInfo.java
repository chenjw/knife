package com.chenjw.knife.core.model.result;

import java.io.Serializable;

public class EntryInfo implements Serializable {

    /**  */
    private static final long serialVersionUID = 234739921069275026L;
    
    private ObjectInfo key;
    
    private ObjectInfo value;

    public ObjectInfo getKey() {
        return key;
    }

    public void setKey(ObjectInfo key) {
        this.key = key;
    }

    public ObjectInfo getValue() {
        return value;
    }

    public void setValue(ObjectInfo value) {
        this.value = value;
    }
    
    
}
