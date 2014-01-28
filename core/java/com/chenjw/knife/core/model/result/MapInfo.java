package com.chenjw.knife.core.model.result;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.chenjw.knife.core.model.divide.Dividable;

public class MapInfo implements Dividable, Serializable{

    /**  */
    private static final long serialVersionUID = -625937565722599225L;
    private EntryInfo[] elements;

    @Override
    public void divide(List<Object> fragments) {

        if (elements != null) {
            fragments.add(elements.length);
            for (EntryInfo o : elements) {
                fragments.add(o);
            }
        } else {
            fragments.add(-1);
        }
    }

    @Override
    public void combine(Object[] fragments) {
        int num = (Integer) fragments[0];
        if (num == -1) {
            return;
        } else {

        }
        this.elements = Arrays.copyOfRange(fragments, 1, 1 + num,
            EntryInfo[].class);
    }

    public EntryInfo[] getElements() {
        return elements;
    }

    public void setElements(EntryInfo[] elements) {
        this.elements = elements;
    }
}
