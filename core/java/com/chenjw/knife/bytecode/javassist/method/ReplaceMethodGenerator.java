/*
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.chenjw.knife.bytecode.javassist.method;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

import com.chenjw.knife.bytecode.javassist.MethodGenerator;

/**
 * 方法构建器
 * 
 * @author chenjw 2012-6-13 上午11:46:07
 */
public final class ReplaceMethodGenerator implements MethodGenerator {

    private CtMethod     ctMethod;
    private List<String> methodLines = new ArrayList<String>();

    public static ReplaceMethodGenerator newInstance(
                                                     CtMethod ctMethod) {
        return new ReplaceMethodGenerator(ctMethod);
    }

    public void generate(CtClass ctClass) throws CannotCompileException {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String line : methodLines) {
            sb.append(line);
        }
        sb.append("}");
       
        ctMethod.setBody(sb.toString());
    }


    private ReplaceMethodGenerator(CtMethod ctMethod) {
        this.ctMethod=ctMethod;
    }

    public List<String> getMethodLines() {
        return methodLines;
    }


    /**
     * 表达式语句
     * 
     * @param expr
     */
    public void addExpression(String expression) {
        methodLines.add(expression);
    }

}
